package site.ljc.yygh.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Hamster
 * @Date 2022/4/18 21:22
 * @Version 1.0
 */
@Configuration
@MapperScan("site.ljc.yygh.cmn.mapper")
public class CmnConfig {
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        return new PaginationInnerInterceptor();
    }
}
