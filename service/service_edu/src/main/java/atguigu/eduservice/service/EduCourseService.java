package atguigu.eduservice.service;

import atguigu.eduservice.pojo.EduCourse;
import atguigu.eduservice.pojo.frontvo.CourseFrontVo;
import atguigu.eduservice.pojo.frontvo.CourseWebVo;
import atguigu.eduservice.pojo.vo.CourseInfoVo;
import atguigu.eduservice.pojo.vo.CoursePublishVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
public interface EduCourseService extends IService<EduCourse> {
    //添加课程基本信息
    String saveCourseInfo(CourseInfoVo courseInfoVo);
    //根据课程id查询课程基本信息
    CourseInfoVo getCourseInfo(String courseId);
    //修改课程信息
    void updateCourseInfo(CourseInfoVo courseInfoVo);
    //根据课程id查询课程确认信息
    CoursePublishVo publishCourseInfo(String id);
    //删除课程
    void removeCourse(String courseId);
    //条件分页查询课程
    Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo);
    //课程详情
    CourseWebVo getBaseCourseInfo(String courseId);
}
