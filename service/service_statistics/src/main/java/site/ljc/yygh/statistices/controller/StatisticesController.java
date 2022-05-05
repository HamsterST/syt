package site.ljc.yygh.statistices.controller;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.order.client.OrderFeignClient;
import site.ljc.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/5/5 11:18
 * @Version 1.0
 */
@Controller
@RequestMapping("/admin/statistics")
public class StatisticesController {
    @Autowired
    private OrderFeignClient orderFeignClient;
    //获取预约统计数据
    @GetMapping("getCountMap")
    public Result getCountMap(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> countMap = orderFeignClient.getCountMap(orderCountQueryVo);
        return Result.ok(countMap);
    }
}
