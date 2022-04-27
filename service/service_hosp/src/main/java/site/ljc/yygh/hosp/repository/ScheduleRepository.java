package site.ljc.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.model.hosp.Schedule;

import java.util.Date;
import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/4/22 10:19
 * @Version 1.0
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
