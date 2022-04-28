package site.ljc.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Hamster
 * @Date 2022/4/28 18:03
 * @Version 1.0
 */
@Configuration
@MapperScan("site.ljc.yygh.user.mapper")
public class UserConfig {
}
