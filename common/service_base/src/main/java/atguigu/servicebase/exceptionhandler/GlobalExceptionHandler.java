package atguigu.servicebase.exceptionhandler;

import atguigu.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice //标注一个类为全局处理类
@Slf4j //slfj是个最原始的日志框架接口，后面的 logback、slfj-logfj是对其的实现
public class GlobalExceptionHandler {
    //全局异常处理
    @ExceptionHandler(Exception.class) //标注哪一些异常需要统一处理类来处理(指定出现什么异常执行这个方法)
    @ResponseBody //为了返回数据(Responsebody是将数据响应给页面 不加页面收不到)
    public R error(Exception e) {
        e.printStackTrace();
        return R.error().message("执行了全局异常处理..");
    }
    //特定异常处理
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public R error(ArithmeticException e) {
        e.printStackTrace();
        return R.error().message("执行了ArithmeticException异常处理..");
    }
    //自定义异常处理
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e) {
        //如果程序运行出现异常，把异常信息输出到文件中
        //getMsg:是返回的json提示错误信息；getMessage:返回的是详细的异常信息
        log.error(e.getMessage());
        //StackTraceElement stackTrace = e.getStackTrace()[0]; stackTrace 可以获取出错的类、出错的行数、出错的方法、错误信息
        e.printStackTrace();
        //可以用自定义异常类给前端返回特定的状态码和信息，让前端更好的知道错误的点在哪
        return R.error().code(e.getCode()).message(e.getMsg());
        //假如以后你在项目很庞大的时候忽然出错了，你可以根据异常信息快速定义到出错位置与原因，因为这个异常信息是你自定义写的
    }
}
