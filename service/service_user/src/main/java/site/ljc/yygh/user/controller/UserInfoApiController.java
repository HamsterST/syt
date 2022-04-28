package site.ljc.yygh.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.result.Result;
import site.ljc.yygh.user.service.UserInfoService;
import site.ljc.yygh.vo.user.LoginVo;

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
}
