package site.ljc.yygh.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Hamster
 * @Date 2022/5/1 13:25
 * @Version 1.0
 */
@EnableDiscoveryClient
@ComponentScan(basePackages = {"site.ljc"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class,args);
    }
}
