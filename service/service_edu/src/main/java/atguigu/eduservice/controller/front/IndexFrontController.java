package atguigu.eduservice.controller.front;

import atguigu.commonutils.R;
import atguigu.eduservice.pojo.EduCourse;
import atguigu.eduservice.pojo.EduTeacher;
import atguigu.eduservice.service.EduCourseService;
import atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eduservice/indexfront")
//@CrossOrigin
//在前端模块写的话，想要调用后端写好的功能，就要服务调用，一直服务调用是很耗时的
public class IndexFrontController {
    @Autowired
    private EduCourseService courseService;
    @Autowired
    private EduTeacherService teacherService;

    //查询前8条热门课程，查询前4条名师
    @GetMapping("index")
    public R index() {
        //查询前8条热门课程
        QueryWrapper<EduCourse> wrapperCourse = new QueryWrapper<>();
        wrapperCourse.eq("status", "Normal"); //查询已发布的课程
        wrapperCourse.orderByDesc("view_count" ,"buy_count").last("limit 8");
        List<EduCourse> courseList = courseService.list(wrapperCourse);

        //查询前4条名师
        QueryWrapper<EduTeacher> wrapperTeacher = new QueryWrapper<>();
        wrapperTeacher.orderByDesc("sort").last("limit 4");
        List<EduTeacher> teacherList = teacherService.list(wrapperTeacher);

        return R.ok().data("courseList", courseList).data("teacherList", teacherList);
    }

}
