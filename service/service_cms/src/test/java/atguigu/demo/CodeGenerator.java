package atguigu.demo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;

/**
 * @author
 * @since 2018/12/13
 */
public class CodeGenerator {
    @Test
    public void run() {
        FastAutoGenerator
                // 数据源配置
                .create("jdbc:mysql://localhost:3306/guli", "root", "Cm920124-")
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("testjava") // 设置作者
                            .enableSwagger() // 开启 swagger 模式 默认值:false
                            .fileOverride() // 覆盖已生成文件 默认值:false
                            .disableOpenDir()//禁止打开输出目录 默认值:true
                            .commentDate("yyyy-MM-dd")// 注释日期
                            .dateType(DateType.ONLY_DATE)//定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("atguigu") // 设置父包名
                            .moduleName("educms") // 设置父包模块名 默认值:无
                            .controller("controller")//Controller 包名 默认值:controller
                            .entity("pojo")//Entity 包名 默认值:entity
                            .service("service")//Service 包名 默认值:service
                            .mapper("mapper");//Mapper 包名 默认值:mapper
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("crm_banner") // 设置需要生成的表名 可边长参数“user”, “user1”
                            .serviceBuilder()//service策略配置
                            .formatServiceFileName("%sService")
                            .entityBuilder()// 实体类策略配置
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(IdType.ASSIGN_ID)//主键策略  雪花算法自动生成的id
                            .enableLombok() //开启lombok
                            .enableTableFieldAnnotation()// 属性加上注解说明
                            .controllerBuilder() //controller 策略配置
                            .enableRestStyle() //开启RestController注解
                            .enableHyphenStyle();
                })
                // 执行
                .execute();
    }
}
