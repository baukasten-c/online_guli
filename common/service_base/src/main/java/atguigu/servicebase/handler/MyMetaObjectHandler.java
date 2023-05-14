package atguigu.servicebase.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //根据阿里巴巴规范每张表都有创建和修改时间，而逻辑删除和分页不一定有，所有只有自动填充放service_base
    @Override
    public void insertFill(MetaObject metaObject) {
        //属性名称，不是字段名称(填充原理是直接给pojo的属性设置值)
        this.setFieldValByName("gmtCreate", new Date(), metaObject);
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }
}
