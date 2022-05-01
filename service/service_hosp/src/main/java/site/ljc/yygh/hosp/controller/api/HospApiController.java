package site.ljc.yygh.hosp.controller.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.hosp.service.DepartmentService;
import site.ljc.yygh.hosp.service.HospitalService;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.vo.hosp.DepartmentVo;
import site.ljc.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/27 15:56
 * @Version 1.0
 */
@RestController
@RequestMapping("api/hosp/hosptial")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    @ApiOperation("查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }
    @ApiOperation("根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }
    @ApiOperation("根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
    @ApiOperation("根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String,Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }
}
