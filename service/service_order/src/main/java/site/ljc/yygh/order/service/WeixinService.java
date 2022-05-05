package site.ljc.yygh.order.service;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/5/4 15:31
 * @Version 1.0
 */
public interface WeixinService {
    //生成微信支付二维码
    Map createNative(Long orderId);
    //调用微信接口实现支付状态查询
    Map<String, String> queryPayStatus(Long orderId);
    //退款
    Boolean refund(Long orderId);

}
