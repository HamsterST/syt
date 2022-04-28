package site.ljc.yygh.user.service;

import site.ljc.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/28 17:57
 * @Version 1.0
 */
public interface UserInfoService{
    Map<String, Object> loginUser(LoginVo loginVo);
}
