package atguigu.eduservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "service-ucenter", fallback = UcenterFileDegradeFeignClient.class)
public interface UcenterClient {
    //定义到调用方法的路径
    //根据用户id获取用户信息
    @GetMapping("/educenter/member/getMemberInfoById/{memberId}")
    //@PathVariable注解一定要指定参数名称，否则出错
    Map getUserId(@PathVariable("memberId") String memberId);
}
