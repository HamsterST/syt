package site.ljc.yygh.msm.service;

import site.ljc.yygh.vo.msm.MsmVo;

/**
 * @Author Hamster
 * @Date 2022/4/29 9:42
 * @Version 1.0
 */
public interface MsmService {
    //发送手机验证码
    boolean send(String phone, String code);
    //mq使用发送短信
    boolean send(MsmVo msmVo);

}
