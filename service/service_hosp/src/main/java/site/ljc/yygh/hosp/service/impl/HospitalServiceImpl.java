package site.ljc.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import site.ljc.yygh.cmn.client.DictFeignClient;
import site.ljc.yygh.hosp.repository.HospitalRepository;
import site.ljc.yygh.hosp.service.HospitalService;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.vo.hosp.HospitalQueryVo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/21 15:51
 * @Version 1.0
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        //如果存在，进行修改
        if(hospitalExist != null){
            hospitalExist.setStatus(hospitalExist.getStatus());
            hospitalExist.setCreateTime(hospitalExist.getCreateTime());
            hospitalExist.setUpdateTime(new Date());
            hospitalExist.setIsDeleted(0);
            hospitalRepository.save(hospitalExist);
        }else{
            //如果不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }
    //医院列表(条件查询分页)
    @Override
    public Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageAble对象
        Pageable pageAble = PageRequest.of(page-1,limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching().
                withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        Example<Hospital> example = Example.of(hospital, matcher);
        //调用方法
        Page<Hospital> pages = hospitalRepository.findAll(example, pageAble);
        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item ->{
            this.setHospitalHosType(item);
        });
        return pages;

    }

    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String,Object> getHospById(String id) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Hospital hospital = setHospitalHosType(hospitalRepository.findById(id).get());
        //医院基本信息(包含医院等级)
        result.put("hospital",hospital);
        //单独处理更直观
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostype = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省市地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("provinceString",provinceString);
        hospital.getParam().put("cityString",cityString);
        hospital.getParam().put("districtString",districtString);
        hospital.getParam().put("hostypeString",hostype);

        return hospital;
    }


}
