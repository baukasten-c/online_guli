package atguigu.eduorder.client;

import atguigu.commonutils.ordervo.CourseWebVoOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("service-edu")
public interface EduClient {
    //根据课程id查询课程信息
    @GetMapping("/eduservice/coursefront/getCourseInfoOrder/{id}")
    CourseWebVoOrder getCourseInfoOrder(@PathVariable("id") String id);
}
