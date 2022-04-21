package site.ljc.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import site.ljc.yygh.model.hosp.Hospital;

/**
 * @Author Hamster
 * @Date 2022/4/21 15:30
 * @Version 1.0
 */
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //判断是否存在数据
    Hospital getHospitalByHoscode(String hoscode);

}
