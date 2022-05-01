package site.ljc.yygh.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.common.utils.AuthContextHolder;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.user.service.UserInfoService;
import site.ljc.yygh.vo.user.LoginVo;
import site.ljc.yygh.vo.user.UserAuthVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/28 17:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userInfoService;
    //用户手机号登录接口
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String,Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }
    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }
    //获取用户id信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }

}
