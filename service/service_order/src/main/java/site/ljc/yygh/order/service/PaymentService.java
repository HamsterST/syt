package site.ljc.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.order.OrderInfo;
import site.ljc.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/5/4 15:37
 * @Version 1.0
 */
public interface PaymentService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo order, Integer status);

    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    PaymentInfo getPaymentInfo(Long orderId,Integer paymentType);
}
