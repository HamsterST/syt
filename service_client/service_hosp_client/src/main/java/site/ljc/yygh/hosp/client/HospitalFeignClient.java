package site.ljc.yygh.hosp.client;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.ljc.yygh.vo.hosp.ScheduleOrderVo;
import site.ljc.yygh.vo.order.SignInfoVo;

/**
 * @Author Hamster
 * @Date 2022/5/3 20:30
 * @Version 1.0
 */
@FeignClient("service-hosp")
@Repository
public interface HospitalFeignClient {
    @GetMapping("/api/hosp/hosptial/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(
            @PathVariable("scheduleId") String scheduleId);
    @GetMapping("/api/hosp/hosptial/inner/getSignInfoVo/{hoscode}")
    SignInfoVo getSignInfoVo(
            @PathVariable("hoscode") String hoscode);
}
