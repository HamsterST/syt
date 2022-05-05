package site.ljc.yygh.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netflix.ribbon.proxy.annotation.Http;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.common.utils.AuthContextHolder;
import site.ljc.yygh.enums.OrderStatusEnum;
import site.ljc.yygh.model.order.OrderInfo;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.order.service.OrderService;
import site.ljc.yygh.vo.order.OrderCountQueryVo;
import site.ljc.yygh.vo.order.OrderQueryVo;
import site.ljc.yygh.vo.user.UserInfoQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @Author Hamster
 * @Date 2022/5/3 19:41
 * @Version 1.0
 */
@Controller
@RequestMapping()
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    //生成挂号订单
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result savaOrders(@PathVariable String scheduleId,
                             @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }

    //根据订单id查询订单详情
    @GetMapping("auth/getOrder/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderId);
    }
    //订单列表（条件查询带分页）
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request){
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam,orderQueryVo);
        return Result.ok(pageModel);
    }
    @GetMapping("auth/getStatusList")
    public Result getStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }
    //取消预约
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        boolean isOrder = orderService.cancelOrder(orderId);
        return Result.ok(isOrder);
    }
    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }

}
