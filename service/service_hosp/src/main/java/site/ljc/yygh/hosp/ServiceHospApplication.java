package site.ljc.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Hamster
 * @Date 2022/4/18 19:02
 * @Version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"site.ljc"})
@EnableDiscoveryClient
//使用注解@EnableFeignClients启动feign客户端
@EnableFeignClients(basePackages = {"site.ljc"})
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class,args);
    }
}
