package site.ljc.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.vo.user.LoginVo;
import site.ljc.yygh.vo.user.UserAuthVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/28 17:57
 * @Version 1.0
 */
public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);
}
