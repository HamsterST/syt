package site.ljc.yygh.hosp.controller.api;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.ljc.yygh.common.helper.HttpRequestHelper;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.common.utils.MD5;
import site.ljc.yygh.common.utils.ResultCodeEnum;
import site.ljc.yygh.common.utils.YyghException;
import site.ljc.yygh.hosp.service.DepartmentService;
import site.ljc.yygh.hosp.service.HospitalService;
import site.ljc.yygh.hosp.service.HosptialSetService;

import site.ljc.yygh.hosp.service.ScheduleService;
import site.ljc.yygh.model.hosp.Department;
import site.ljc.yygh.model.hosp.Hospital;
import site.ljc.yygh.model.hosp.Schedule;
import site.ljc.yygh.vo.hosp.DepartmentQueryVo;
import site.ljc.yygh.vo.hosp.ScheduleQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/4/21 15:27
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    HospitalService hospitalService;
    @Autowired
    HosptialSetService hosptialSetService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    ScheduleService scheduleService;
    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //1 获取医院系统传递过来的签名
        String sign = (String) paramMap.get("sign");
        //2 根据传递过来医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hosptialSetService.getSignKey(hoscode);
        //3 把数据库查询签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        //4 判断签名是否一致
        if(!sign.equals(encrypt)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中“+”转换为“ ”,因此我们要转换回来
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData",logoData);

        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
    //查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        String signKey = hosptialSetService.getSignKey(hoscode);
        //3 把数据库查询签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        //4 判断签名是否一致
        if(!sign.equals(encrypt)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法实现根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }
    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        String signKey = hosptialSetService.getSignKey(hoscode);
        //3 把数据库查询签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        //4 判断签名是否一致
        if(!sign.equals(encrypt)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service的方法
        departmentService.save(paramMap);
        return Result.ok();
    }
    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号
        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isBlank((String)paramMap.get("page")) ?
                1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isBlank((String)paramMap.get("limit")) ?
                1 : Integer.parseInt((String) paramMap.get("limit"));
        String sign = (String) paramMap.get("sign");
        String signKey = hosptialSetService.getSignKey(hoscode);
        //3 把数据库查询签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        //4 判断签名是否一致
        if(!sign.equals(encrypt)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //调用service的方法
        Page<Department> pageModel = departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }
//    删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号 和 科室编号
        String hoscdoe = (String)paramMap.get("hoscdoe");
        String depcode = (String)paramMap.get("depcode");
        //TODO 签名校验
        departmentService.remove(hoscdoe,depcode);
        return Result.ok();
    }
//    上传排班
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //TODO 签名校验
       scheduleService.save(paramMap);
        return Result.ok();
    }
//    查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        int page = StringUtils.isBlank((String)paramMap.get("page")) ?
                1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isBlank((String)paramMap.get("limit")) ?
                1 : Integer.parseInt((String) paramMap.get("limit"));
        String sign = (String) paramMap.get("sign");
        String signKey = hosptialSetService.getSignKey(hoscode);
        //3 把数据库查询签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        //4 判断签名是否一致
        if(!sign.equals(encrypt)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setDepcode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        //调用Service方法
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }
    //删除排产
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号和排班编号
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();


    }

}
