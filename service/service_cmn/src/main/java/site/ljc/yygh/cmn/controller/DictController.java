package site.ljc.yygh.cmn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.ljc.yygh.cmn.service.DictService;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/4/20 10:10
 * @Version 1.0
 */
@Api("数据字典管理")
@RestController
@RequestMapping("admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;
    //根据数据id查询子数据列表
    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }
    //导出数据字典接口
    @GetMapping("exportData")
    public Result exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
        return Result.ok();
    }
    //导入数据字典
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }
    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }
    //根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName("",value);
        return dictName;
    }

}
