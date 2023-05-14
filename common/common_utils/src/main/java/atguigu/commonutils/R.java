package atguigu.commonutils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
    统一结果返回
    {
        “success"：布尔   //响应是否成功
        "code"：数字      //响应码
        "message"：字符串 //返回消息
        "date"：HashMap  //返回数据(放在键值对中)
 */
@Data
public class R implements Serializable { //R为result的缩写

    @ApiModelProperty(value = "是否成功") //@ApiModelProperty("类属性注释内容")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    //把构造方法私有(这样是规定不能使用本类构造方法，只能用ok或者error)
    private R() {}
    //不是单例，单例三要素只满足构造函数私有化，这里的每次调用静态方法都会创建新的R对象
    //成功静态方法
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    //失败静态方法
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }
    //链式编程
    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
