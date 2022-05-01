package site.ljc.yygh.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.ljc.yygh.common.result.Result;
import site.ljc.yygh.oss.service.FileService;
import site.ljc.yygh.oss.utils.ConstantOssPropertiesUtils;

/**
 * @Author Hamster
 * @Date 2022/5/1 13:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {
    @Autowired
    private FileService fileService;
    //上传文件到阿里云oss
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
