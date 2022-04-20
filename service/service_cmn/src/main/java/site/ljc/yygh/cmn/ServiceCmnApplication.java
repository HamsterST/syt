package site.ljc.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Hamster
 * @Date 2022/4/20 9:42
 * @Version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "site.ljc")
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class,args);
    }
}
