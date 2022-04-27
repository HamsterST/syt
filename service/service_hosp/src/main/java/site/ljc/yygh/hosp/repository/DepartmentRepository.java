package site.ljc.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import site.ljc.yygh.model.hosp.Department;

/**
 * @Author Hamster
 * @Date 2022/4/21 19:04
 * @Version 1.0
 */
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
