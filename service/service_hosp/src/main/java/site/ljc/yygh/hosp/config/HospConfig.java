package site.ljc.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Hamster
 * @Date 2022/4/18 21:22
 * @Version 1.0
 */
@Configuration
@MapperScan("site.ljc.yygh.hosp.mapper")
public class HospConfig {
}
