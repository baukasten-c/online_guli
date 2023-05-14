package atguigu.eduservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//Springboot默认只会扫描启动类所在包极其子包下的带有@Component注解的类
@ComponentScan(basePackages = {"atguigu"})
@EnableDiscoveryClient //nacos注册
@EnableFeignClients //激活OpenFeign
//@EnableTransactionManagement
public class EduApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class, args);
    }
}
