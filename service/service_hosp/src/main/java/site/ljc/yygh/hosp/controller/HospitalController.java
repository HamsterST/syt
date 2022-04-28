package site.ljc.yygh.hosp.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.result.result.Result;
import site.ljc.yygh.hosp.service.HospitalService;
import site.ljc.yygh.vo.hosp.HospitalQueryVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/24 18:54
 * @Version 1.0
 */
@Api(tags = "医院管理")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    //医院列表(条件查询分页)
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }
    //医院详情信息
    @GetMapping("showHospDetial/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String, Object> hospDetail = hospitalService.getHospById(id);
        return Result.ok(hospDetail);
    }
}
