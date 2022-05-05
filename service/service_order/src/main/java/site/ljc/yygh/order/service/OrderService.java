package site.ljc.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Ordering;
import site.ljc.yygh.model.order.OrderInfo;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.vo.order.OrderCountQueryVo;
import site.ljc.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/5/3 19:34
 * @Version 1.0
 */
public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    boolean cancelOrder(Long orderId);

    void patientTips();

    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
