package site.ljc.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Ordering;
import site.ljc.yygh.model.order.OrderInfo;

/**
 * @Author Hamster
 * @Date 2022/5/3 19:34
 * @Version 1.0
 */
public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);
}
