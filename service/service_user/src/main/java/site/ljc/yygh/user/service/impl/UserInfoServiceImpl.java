package site.ljc.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.ljc.yygh.common.result.helper.JwtHelper;
import site.ljc.yygh.common.utils.ResultCodeEnum;
import site.ljc.yygh.common.utils.YyghException;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.user.mapper.UserInfoMapper;
import site.ljc.yygh.user.service.UserInfoService;
import site.ljc.yygh.vo.user.LoginVo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/28 17:57
 * @Version 1.0
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    //用户手机号登录接口
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        HashMap<String, Object> map = null;
        //从loginVo获取输入的手机号，和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //判断手机号和验证码是否为空
        if(StringUtils.isBlank(phone)||StringUtils.isBlank(code)){
            throw  new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        //TODO 判断手机验证码和输入的验证码是否一致
        //判断是否第一次登录：根据手机查询数据库，如果不存在相同手机号就是第一次登录
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone",phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if(userInfo == null){
            //添加信息到数据库
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }else{
            //不是第一次，直接登录
            if(userInfo.getStatus() == 0){
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
             map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isBlank(name)){
                name = userInfo.getNickName();
            }
            if(StringUtils.isBlank(name)){
                name = userInfo.getPhone();
            }
            //返回登录信息
            map.put("name",name);
            //jwt生成toekn字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token",token);
        }
        return map;
    }
}
