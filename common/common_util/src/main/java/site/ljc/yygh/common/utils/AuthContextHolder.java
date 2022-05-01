package site.ljc.yygh.common.utils;

import site.ljc.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Hamster
 * @Date 2022/5/1 21:11
 * @Version 1.0
 */
//获取当前用户信息工具类
public class AuthContextHolder {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取uesrid
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }
    //获取当前用户名称
    public static String getUserName(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取uesrid
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
