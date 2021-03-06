package site.ljc.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import site.ljc.yygh.common.result.ResultCodeEnum;
import site.ljc.yygh.common.utils.YyghException;
import site.ljc.yygh.hosp.mapper.ScheduleMapper;
import site.ljc.yygh.hosp.repository.ScheduleRepository;
import site.ljc.yygh.hosp.service.DepartmentService;
import site.ljc.yygh.hosp.service.HospitalService;
import site.ljc.yygh.hosp.service.ScheduleService;
import site.ljc.yygh.model.hosp.BookingRule;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.model.hosp.Schedule;
import site.ljc.yygh.vo.hosp.BookingScheduleRuleVo;
import site.ljc.yygh.vo.hosp.ScheduleOrderVo;
import site.ljc.yygh.vo.hosp.ScheduleQueryVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Hamster
 * @Date 2022/4/22 10:21
 * @Version 1.0
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper,Schedule>implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(mapString, Schedule.class);
        //????????????????????????
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        //???????????????????????????
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            //??????????????????????????????
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //??????Pageable??????????????????????????????????????????
        Pageable pageable = PageRequest.of(page - 1, limit);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        //??????Example??????
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    //????????????
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }

    }

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //1 ????????????????????????????????? ??????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //2 ???????????????workDate???????????????
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        //first????????????workDate??????????????????
                        .first("workDate").as("workDate")
                        //??????????????????
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //??????
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //???????????????????????????
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //4 ????????????
        AggregationResults<BookingScheduleRuleVo> aggResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();
        //???????????????????????????
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
        //???????????????????????????
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //?????????????????????????????????
        HashMap<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);
        //??????????????????
        String hosName = hospitalService.getHospName(hoscode);
        //??????????????????
        HashMap<String, String> baseMap = new HashMap<>();
        baseMap.put("hosName", hosName);
        result.put("baseMap", baseMap);
        return result;


    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        List<Schedule> schedules =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        schedules.stream().forEach(item -> {
            packageSchedule(item);
        });
        return schedules;

    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();
        //??????????????????
        //????????????????????????????????????
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //??????????????????????????????????????????
        IPage iPage = getListDate(page, limit, bookingRule);
        //?????????????????????
        List<Date> dateList = iPage.getRecords();
        //???????????????????????????????????????????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber"));
        AggregationResults<BookingScheduleRuleVo> aggregateResult = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();
        //????????????  map?????? key??????  value??????????????????????????????
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().
                    collect(
                            Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                                    BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }
        //???????????????????????????
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            //???map????????????key????????????value???
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //??????????????????????????????
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //??????????????????
                bookingScheduleRuleVo.setDocCount(0);
                //?????????????????????  -1????????????
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //????????????????????????????????????
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //?????????????????????????????????????????????   ?????? 0????????? 1??????????????? -1????????????????????????
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //??????????????????????????????????????? ????????????
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    //????????????
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //???????????????????????????
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());

        //??????????????????
        Map<String, String> baseMap = new HashMap<>();
        //????????????
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //??????
        Department department = departmentService.getDepartment(hoscode, depcode);
        //???????????????
        baseMap.put("bigname", department.getBigname());
        //????????????
        baseMap.put("depname", department.getDepname());
        //???
        baseMap.put("workDateString", new DateTime().toString("yyyy???MM???"));
        //????????????
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //????????????
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;

    }

    @Override
    public Schedule getScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        return packageSchedule(schedule);
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        Schedule schedule = baseMapper.selectById(scheduleId);
        if(schedule == null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if(hospital == null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        if(bookingRule == null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //????????????????????????scheduleOrderVo
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalService.getHospName(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());
        //?????????????????????????????????????????????-1????????????0???
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //??????????????????
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //??????????????????
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //????????????????????????
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        return scheduleOrderVo;
    }

    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        //????????????????????????
        DateTime releaseTime = getDateTime(new Date(), bookingRule.getReleaseTime());
        //??????????????????
        Integer cycle = bookingRule.getCycle();
        //????????????????????????????????????????????????????????????????????????????????????+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        List<Date> dateList = new ArrayList<>();
        for (Integer i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //???????????????????????????????????????????????????7??????????????????7?????????
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        //??????????????????????????????7
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = 0; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        IPage<Date> ipage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, dateList.size());
        ipage.setRecords(pageDateList);
        return ipage;


    }

    /**
     * ???Date?????????yyyy-MM-dd HH:mm????????????DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private Schedule packageSchedule(Schedule schedule) {
        //??????????????????
        schedule.getParam().put("hosName", hospitalService.getHospName(schedule.getHoscode()));
        //??????????????????
        schedule.getParam().put("depName", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //????????????????????????
        schedule.getParam().put("dayOfWeek", getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;

    }

    /**
     * ??????????????????????????????
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "??????";
            default:
                break;
        }
        return dayOfWeek;
    }
}
