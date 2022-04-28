package site.ljc.yygh.hosp.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.ljc.yygh.common.result.result.Result;
import site.ljc.yygh.hosp.service.DepartmentService;
import site.ljc.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/4/26 16:34
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    //根据医院编号，查询医院所有科室列表
    @ApiOperation("查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);

    }
}
