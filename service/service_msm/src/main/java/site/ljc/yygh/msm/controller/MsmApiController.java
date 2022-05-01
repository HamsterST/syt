package site.ljc.yygh.msm.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.msm.service.MsmService;
import site.ljc.yygh.msm.utils.RandomUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author Hamster
 * @Date 2022/4/29 9:40
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码，如果获取到，返回ok
        //key 手机号 value 验证码
        String s = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isBlank(s)){
            return Result.ok();
        }
        //如edis获取不到
        //生成验证码，通过整合短信服务进行发送
        String code = RandomUtil.getSixBitRandom();
        boolean isSend = msmService.send(phone,code);
        if(isSend){
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return Result.ok();
        }else {
            return Result.fail().message("发送短信失败");
        }
    }

}
