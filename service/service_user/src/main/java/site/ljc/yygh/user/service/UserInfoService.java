package site.ljc.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.vo.user.LoginVo;
import site.ljc.yygh.vo.user.UserAuthVo;
import site.ljc.yygh.vo.user.UserInfoQueryVo;

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

    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}
