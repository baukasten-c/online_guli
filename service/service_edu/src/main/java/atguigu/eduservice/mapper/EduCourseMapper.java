package atguigu.eduservice.mapper;

import atguigu.eduservice.pojo.EduCourse;
import atguigu.eduservice.pojo.frontvo.CourseWebVo;
import atguigu.eduservice.pojo.vo.CoursePublishVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    //根据课程id查询课程确认信息
    CoursePublishVo getPublishCourseInfo(String courseId);
    //根据课程id查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
