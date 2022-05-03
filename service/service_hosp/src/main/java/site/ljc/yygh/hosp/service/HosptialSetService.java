package site.ljc.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.hosp.HospitalSet;
import site.ljc.yygh.vo.order.SignInfoVo;

/**
 * @Author Hamster
 * @Date 2022/4/18 21:06
 * @Version 1.0
 */
public interface HosptialSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
