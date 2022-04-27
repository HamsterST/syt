package site.ljc.yygh.hosp.service;

import org.springframework.data.domain.Page;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
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

    Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String,Object> getHospById(String id);

    String getHospName(String hoscode);

    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);
}
