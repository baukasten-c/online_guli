package atguigu.commonutils;

public interface ResultCode {
    //枚举类是定义常量,私有化构造函数,这也给对象属性给予固定的值,保证返回前端的信息是固定的,但是这里的属性值必须是固定的那就使用常量比较合适
    //接口的字段默认都是public static final的
    Integer SUCCESS = 20000; //成功
    Integer ERROR = 20001; //失败
}
