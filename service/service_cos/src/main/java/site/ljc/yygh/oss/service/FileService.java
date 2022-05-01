package site.ljc.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Hamster
 * @Date 2022/5/1 13:39
 * @Version 1.0
 */
public interface FileService {
    String upload(MultipartFile file);
}
