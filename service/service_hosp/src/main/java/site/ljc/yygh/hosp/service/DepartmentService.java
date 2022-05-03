package site.ljc.yygh.hosp.service;


import org.springframework.data.domain.Page;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.vo.hosp.DepartmentQueryVo;
import site.ljc.yygh.vo.hosp.DepartmentVo;

import java.util.List;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/21 19:05
 * @Version 1.0
 */
public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscdoe, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
