package site.ljc.yygh.hosp.service;

import org.springframework.data.domain.Page;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.model.hosp.Schedule;
import site.ljc.yygh.vo.hosp.ScheduleQueryVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/22 10:20
 * @Version 1.0
 */
public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);
}
