package site.ljc.yygh.hosp.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.result.Result;
import site.ljc.yygh.hosp.service.ScheduleService;
import site.ljc.yygh.model.hosp.Schedule;

import java.util.List;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/27 9:27
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    //根据医院编号和科室编号，查询排班规则数据
    @ApiOperation("查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
        Map<String,Object> scheduleRuleReuslt = scheduleService.getRuleSchedule(page,limit,hoscode,depcode);
        return Result.ok(scheduleRuleReuslt);
    }
    //根据医院编号、科室编号和工作日期，查询排版详细信息
    @ApiOperation("查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
