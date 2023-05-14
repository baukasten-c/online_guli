package atguigu.educenter.pojo.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="登录对象", description="登录对象")
/*
    数据库返回给前端的数据有时候是表中的全部字段，这样相当于暴露了表结构。
    接收数据的实体类一般不能直接使用表的映射类，这样也会暴露表结构，而且还增大了网络传输成本。
    所以使用vo(Dto是返回类，Vo是接收类)
 */
public class LoginVo {
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "密码")
    private String password;
}