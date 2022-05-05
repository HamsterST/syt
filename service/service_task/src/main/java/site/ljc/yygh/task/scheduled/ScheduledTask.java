package site.ljc.yygh.task.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.ljc.common.rabbit.contant.MqConst;
import site.ljc.common.rabbit.service.RabbitService;

/**
 * @Author Hamster
 * @Date 2022/5/5 10:32
 * @Version 1.0
 */
@Component
@EnableScheduling
public class ScheduledTask {
    @Autowired
    private RabbitService rabbitService;
    //每天8点执行方法，就医提醒
    //cron表达式，设置执行间隔
    //008**
    @Scheduled(cron = "0/30****?")
    public void taskPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
