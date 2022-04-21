package site.ljc.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.ljc.yygh.hosp.mapper.HospitalSetMapper;
import site.ljc.yygh.hosp.service.HosptialSetService;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.model.hosp.HospitalSet;

/**
 * @Author Hamster
 * @Date 2022/4/18 21:10
 * @Version 1.0
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HosptialSetService {
    @Autowired
    BaseMapper baseMapper;
    @Override
    public String getSignKey(String hoscode) {
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HospitalSet::getHoscode,hoscode);
        Hospital hospital = (Hospital) baseMapper.selectOne(queryWrapper);
        return hospital.getHoscode();
    }
}
