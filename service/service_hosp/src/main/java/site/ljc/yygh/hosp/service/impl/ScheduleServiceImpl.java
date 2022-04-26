package site.ljc.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import site.ljc.yygh.hosp.repository.ScheduleRepository;
import site.ljc.yygh.hosp.service.ScheduleService;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.model.hosp.Schedule;
import site.ljc.yygh.vo.hosp.ScheduleQueryVo;

import java.util.Date;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/22 10:21
 * @Version 1.0
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(mapString, Schedule.class);
        //判断是否存在数据
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        //如果存在，进行修改
        if(scheduleExist != null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else{
            //如果不存在，进行添加
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page-1,limit);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example,pageable);
        return  all;
    }
    //删除排班
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }

    }
}
