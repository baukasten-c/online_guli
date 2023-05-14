package atguigu.servicebase.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//data注解里面有无参构造函数，如果需要有参构造函数需要自己定义，但是如果你加了有参以后，无参也要手动添加
@AllArgsConstructor  //生成有参数构造方法
@NoArgsConstructor   //生成无参数构造
public class GuliException extends RuntimeException {
    private Integer code; //状态码
    private String msg; //异常信息
}
