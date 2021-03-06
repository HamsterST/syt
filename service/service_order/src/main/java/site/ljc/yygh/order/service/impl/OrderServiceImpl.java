package site.ljc.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.ljc.common.rabbit.contant.MqConst;
import site.ljc.common.rabbit.service.RabbitService;
import site.ljc.yygh.common.exception.HospitalException;
import site.ljc.yygh.common.helper.HttpRequestHelper;
import site.ljc.yygh.common.result.ResultCodeEnum;
import site.ljc.yygh.enums.OrderStatusEnum;
import site.ljc.yygh.hosp.client.HospitalFeignClient;
import site.ljc.yygh.model.order.OrderInfo;
import site.ljc.yygh.model.user.Patient;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.order.mapper.OrderMapper;
import site.ljc.yygh.order.service.OrderService;
import site.ljc.yygh.order.service.WeixinService;
import site.ljc.yygh.user.client.PatientFeignClient;
import site.ljc.yygh.vo.hosp.ScheduleOrderVo;
import site.ljc.yygh.vo.msm.MsmVo;
import site.ljc.yygh.vo.order.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Author Hamster
 * @Date 2022/5/3 19:38
 * @Version 1.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private HospitalFeignClient hospitalFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WeixinService weixinService;
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //?????????????????????
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //????????????????????????
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //???????????????????????????????????????
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new HospitalException(ResultCodeEnum.TIME_NO);
        }

        //??????????????????
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //??????????????????
        OrderInfo orderInfo = new OrderInfo();
        //scheduleOrderVo ??????????????? orderInfo
        BeanUtils.copyProperties(scheduleOrderVo,orderInfo);
        //???orderInfo??????????????????
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //?????????????????????????????????????????????
        //???????????????????????????????????????????????????map??????
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());

        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //?????????
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        //????????????????????????
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        if(result.getInteger("code")==200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //??????????????????????????????????????????????????????
            String hosRecordId = jsonObject.getString("hosRecordId");
            //????????????
            Integer number = jsonObject.getInteger("number");;
            //????????????
            String fetchTime = jsonObject.getString("fetchTime");;
            //????????????
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //????????????
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //??????????????????
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //?????????????????????
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //??????mq????????????????????????????????????
            //??????mq??????????????????
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //????????????
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "??????" : "??????");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);

            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();

    }

    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return packageOrderInfor(orderInfo);
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //UserInfoQueryVo???????????????
        String name = orderQueryVo.getKeyword();
        String patientName = orderQueryVo.getPatientName();
        String orderStatus = orderQueryVo.getOrderStatus();
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //???????????????????????????
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isEmpty(name), "hosname", name)
                .eq(StringUtils.isEmpty(orderStatus), "order_status", orderQueryVo)
                .eq(StringUtils.isEmpty(patientName), "patient_name", patientName)
                .eq(StringUtils.isEmpty(reserveDate), "reseve_date", patientName)
                .ge(StringUtils.isEmpty(createTimeBegin), "create_time", createTimeBegin)
                .le(StringUtils.isEmpty(createTimeEnd),"create_time",createTimeEnd);
        Page<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        pages.getRecords().stream().forEach(item -> {
            packageOrderInfor(item);
        });
        return pages;
    }
    //????????????
    @Override
    public boolean cancelOrder(Long orderId) {
        //??????????????????
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //??????????????????
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()) {
            throw new HospitalException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //????????????????????????????????????
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(reqMap,
                signInfoVo.getApiUrl()+"/order/updateCancelStatus");
        //??????????????????????????????
        if(result.getInteger("code")!=200) {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            //????????????????????????????????????
            if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                Boolean isRefund = weixinService.refund(orderId);
                if(!isRefund) {
                    throw new HospitalException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
                //??????????????????
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);

                //??????mq??????????????????
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                //????????????
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
                String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "??????": "??????");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                }};
                msmVo.setParam(param);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            }
        }
        return true;
    }
    //????????????
    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        for(OrderInfo orderInfo:orderInfoList) {
            //????????????
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "??????": "??????");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }

    //????????????
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //??????mapper??????????????????
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);

        //??????x???????????? ???????????????  list??????
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());

        //??????y???????????????????????????  list??????
        List<Integer> countList =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }


    private OrderInfo packageOrderInfor(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }


}
