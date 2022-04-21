package site.ljc.yygh.hosp.service;

import site.ljc.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/21 15:50
 * @Version 1.0
 */
public interface HospitalService {
    //上传医院接口
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);
}
