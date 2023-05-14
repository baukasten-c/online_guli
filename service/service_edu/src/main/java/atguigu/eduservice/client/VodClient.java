package atguigu.eduservice.client;

import atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-vod", fallback = VodFileDegradeFeignClient.class) //调用的服务名称
//@Component //这里不用加@Component，因为SpringCloud会自动将@FeignClient注解的接口创建动态代理对象
public interface VodClient {
    //根据视频id删除阿里云视频
    @DeleteMapping("/eduvod/video/removeAlyVideo/{id}") //定义调用的方法路径(完全路径)
    //@PathVariable注解一定要指定参数名称，否则出错
    public R removeAlyVideo(@PathVariable("id") String id);

    //删除多个阿里云视频
    @DeleteMapping("/eduvod/video/delete-batch")
    //使用Feign调用服务时，接口中的方法参数前必须要加注解
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList); //参数多个视频id  List videoIdList
}
