package site.ljc.yygh.user.service.impl;

import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.ljc.yygh.common.helper.JwtHelper;
import site.ljc.yygh.common.utils.YyghException;
import site.ljc.yygh.enums.AuthStatusEnum;
import site.ljc.yygh.model.user.Patient;
import site.ljc.yygh.model.user.UserInfo;
import site.ljc.yygh.user.mapper.UserInfoMapper;
import site.ljc.yygh.user.service.PatientService;
import site.ljc.yygh.user.service.UserInfoService;
import site.ljc.yygh.vo.user.LoginVo;
import site.ljc.yygh.vo.user.UserAuthVo;
import site.ljc.yygh.vo.user.UserInfoQueryVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/28 17:57
 * @Version 1.0
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientService patientService;
    //用户手机号登录接口
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        HashMap<String, Object> map = null;
        //从loginVo获取输入的手机号，和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        // 判断手机验证码和输入的验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            throw new YyghException(ResultCodeEnum.FAIL);
        }
        //判断是否第一次登录：根据手机查询数据库，如果不存在相同手机号就是第一次登录
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if (userInfo == null) {
            //添加信息到数据库
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        } else {
            //不是第一次，直接登录
            if (userInfo.getStatus() == 0) {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
            map = new HashMap<>();
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            //返回登录信息
            map.put("name", name);
            //jwt生成toekn字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
        }
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        //认证人姓名
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);

    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();
        //对条件进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isEmpty(name), "name", name)
                .eq(StringUtils.isEmpty(status), "status", status)
                .eq(StringUtils.isEmpty(authStatus), "auth_status", authStatus)
                .ge(StringUtils.isEmpty(createTimeBegin), "create_time", createTimeBegin);
//                .le(StringUtils.isEmpty(createTimeEnd),"create_time",createTimeEnd)
        Page<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        pages.getRecords().stream().forEach(item -> {
            packageUserInfor(item);
        });
        return pages;

    }
    //用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue()==0||status.intValue()==1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //根据userId查询用户信息
        UserInfo userInfo = packageUserInfor(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList",patientList);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue() ==2 ||authStatus.intValue()==-1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }

    }

    private UserInfo packageUserInfor(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authoStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态0 1
        String status = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",status);
        return userInfo;
    }
}
