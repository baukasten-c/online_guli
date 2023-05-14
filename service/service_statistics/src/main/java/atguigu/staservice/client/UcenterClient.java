package atguigu.staservice.client;

import atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-ucenter")
public interface UcenterClient {
    //查询某一天注册人数
    @GetMapping("/educenter/member/countRegister/{day}")
    R countRegister(@PathVariable("day") String day);
}
