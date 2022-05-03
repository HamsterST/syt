package site.ljc.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;

/**
 * @Author Hamster
 * @Date 2022/5/3 19:40
 * @Version 1.0
 */
@Component
@MapperScan("site.ljc.yygh.order.mapper")
public class OrderConfig {

}
