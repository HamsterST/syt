package site.ljc.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import site.ljc.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/4/20 10:07
 * @Version 1.0
 */
public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);
}
