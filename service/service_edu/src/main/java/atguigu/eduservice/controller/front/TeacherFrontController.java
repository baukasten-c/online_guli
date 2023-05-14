package atguigu.eduservice.controller.front;

import atguigu.commonutils.R;
import atguigu.eduservice.pojo.EduCourse;
import atguigu.eduservice.pojo.EduTeacher;
import atguigu.eduservice.service.EduCourseService;
import atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/teacherfront")
//@CrossOrigin
public class TeacherFrontController {
    @Autowired
    private EduTeacherService teacherService;
    @Autowired
    private EduCourseService courseService;

    //分页查询讲师
    @GetMapping("getTeacherFrontList/{page}/{limit}")
    public R getTeacherFrontList(@PathVariable long page, @PathVariable long limit) {
        Page<EduTeacher> pageTeacher = new Page<>(page, limit);
        Map<String, Object> map = teacherService.getTeacherFrontList(pageTeacher);
        //返回分页所有数据(map和对象一样都会自动转成json)
        //直接放page没有下一页和上一页这两个数据
        return R.ok().data(map);
    }

    //讲师详情的功能
    @GetMapping("getTeacherFrontInfo/{teacherId}")
    public R getTeacherFrontInfo(@PathVariable String teacherId) {
        //根据讲师id查询讲师基本信息
        EduTeacher eduTeacher = teacherService.getById(teacherId);
        //根据讲师id查询所讲课程
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "Normal"); //查询已发布的课程
        wrapper.eq("teacher_id", teacherId);
        wrapper.orderByDesc("view_count");
        List<EduCourse> courseList = courseService.list(wrapper);
        return R.ok().data("teacher", eduTeacher).data("courseList", courseList);
    }
}












