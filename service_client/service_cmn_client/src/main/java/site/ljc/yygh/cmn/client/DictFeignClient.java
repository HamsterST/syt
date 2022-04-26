package site.ljc.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Hamster
 * @Date 2022/4/25 20:09
 * @Version 1.0
 */
@FeignClient("service-cmn")
@Repository
public interface DictFeignClient {
    //根据dictcode和value查询
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value);
    //根据value查询
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable String value);

}
