package atguigu.oss.utils;

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "aliyun.oss.file")
//@Setter //配置类set方法,会出现配置类能被spring扫描到，但值无法注入问题
//当项目已启动，spring接口，spring加载之后，执行接口一个方法
public class ConstantPropertiesUtils implements InitializingBean {
    //读取配置文件内容(先定义成private对数据保护)
    //如果只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用@Value，如果要编写了一个javabean和配置文件进行映射，就直接使用ConfigurationProperties
    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.file.keyid}")
    private String keyId;
    @Value("${aliyun.oss.file.keysecret}")
    private String keySecret;
    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;
    //定义公开静态常量
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;
    @Override
    public void afterPropertiesSet(){ //能读取到配置文件中的值又可以用内容调用
        END_POINT = endpoint;
        ACCESS_KEY_ID = keyId;
        ACCESS_KEY_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}
