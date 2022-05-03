package site.ljc.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Page;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.model.hosp.Schedule;
import site.ljc.yygh.vo.hosp.ScheduleOrderVo;
import site.ljc.yygh.vo.hosp.ScheduleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/22 10:20
 * @Version 1.0
 */
public interface ScheduleService extends IService<Schedule> {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String,Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getScheduleId(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);
}
