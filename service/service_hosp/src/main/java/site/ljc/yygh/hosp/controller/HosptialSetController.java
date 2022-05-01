package site.ljc.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.ljc.yygh.common.utils.MD5;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.hosp.service.HosptialSetService;
import site.ljc.yygh.model.hosp.HospitalSet;
import site.ljc.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Random;

/**
 * @Author Hamster
 * @Date 2022/4/18 21:15
 * @Version 1.0
 */
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HosptialSetController {
    @Autowired
    private HosptialSetService hosptialSetService;

    //1 查询医院设置表所有信息
    @ApiOperation("获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHosptialSet(){
        List<HospitalSet> list = hosptialSetService.list();
        return Result.ok(list);
    }
    //2 逻辑删除医院设置
    @ApiOperation("逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable("id") Long id){
        boolean b = hosptialSetService.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageHospSet(@PathVariable Long current,
                                  @PathVariable Long limit,
                                  @RequestBody(required = false) HospitalQueryVo hospitalQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> hospitalSetPage = new Page<>(current,limit);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(hospitalQueryVo.getHoscode()),"hoscode",hospitalQueryVo.getHoscode())
                .like(StringUtils.isNotBlank(hospitalQueryVo.getHoscode()),"hosname",hospitalQueryVo.getHosname());
        Page<HospitalSet> page = hosptialSetService.page(hospitalSetPage, queryWrapper);
        return Result.ok(page);
    }
    //4 添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态1 使用0不能使用
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        //调用service
        boolean save = hosptialSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    //5 根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        HospitalSet hospitalSet = hosptialSetService.getById(id);
        return Result.ok(hospitalSet);
    }
    //6 修改医院设置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hosptialSetService.updateById(hospitalSet);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    //7 批量删除医院设置
    @DeleteMapping("bathRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        hosptialSetService.removeByIds(idList);
        return Result.ok();
    }
    //8 医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable String id,@PathVariable Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hosptialSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hosptialSetService.updateById(hospitalSet);
        return Result.ok();
    }
    //9 发送签名密钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id){
        HospitalSet hospitalSet = hosptialSetService.getById(id);
        String singKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }

}
