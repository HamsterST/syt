package site.ljc.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.order.PaymentInfo;
import site.ljc.yygh.model.order.RefundInfo;

/**
 * @Author Hamster
 * @Date 2022/5/4 17:05
 * @Version 1.0
 */
public interface RefundInfoService extends IService<RefundInfo> {
    //保存退款记录
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
