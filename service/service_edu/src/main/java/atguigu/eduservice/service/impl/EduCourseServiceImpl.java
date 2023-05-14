package atguigu.eduservice.service.impl;

import atguigu.eduservice.pojo.EduCourse;
import atguigu.eduservice.mapper.EduCourseMapper;
import atguigu.eduservice.pojo.EduCourseDescription;
import atguigu.eduservice.pojo.frontvo.CourseFrontVo;
import atguigu.eduservice.pojo.frontvo.CourseWebVo;
import atguigu.eduservice.pojo.vo.CourseInfoVo;
import atguigu.eduservice.pojo.vo.CoursePublishVo;
import atguigu.eduservice.service.EduChapterService;
import atguigu.eduservice.service.EduCourseDescriptionService;
import atguigu.eduservice.service.EduCourseService;
import atguigu.eduservice.service.EduVideoService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
@Service
//@Transactional(rollbackFor = Exception.class) //注解在代码执行出错的时候能够进行事务的回滚
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {
    @Autowired
    private EduCourseDescriptionService courseDescriptionService;
    @Autowired
    private EduVideoService eduVideoService;
    @Autowired
    private EduChapterService chapterService;

    //添加课程基本信息
    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        //向课程表添加课程基本信息
        EduCourse eduCourse = new EduCourse();
        //copy，对应的值复制，如果没有对应的值或者复制类多出的字段都是不会进行复制的
        BeanUtils.copyProperties(courseInfoVo, eduCourse); //CourseInfoVo对象转换eduCourse对象
        //mapper里的方法是insert，save是service里方法
        int insert = baseMapper.insert(eduCourse);
        if (insert == 0) { //添加失败
            throw new GuliException(20001, "添加课程信息失败");
        }

        //获取添加之后课程id
        String cid = eduCourse.getId();

        //向课程简介表添加课程简介(需要两张表原因：简介内容多,数据多响应效率)
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        //设置描述id就是课程id，使课程和描述是一对一的关系
        courseDescription.setId(cid);
        courseDescriptionService.save(courseDescription);

        return cid;
    }

    //根据课程id查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        BeanUtils.copyProperties(eduCourse, courseInfoVo);
        //查询描述表
        EduCourseDescription description = courseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(description.getDescription());
        return courseInfoVo;
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        int update = baseMapper.updateById(eduCourse);
        if (update == 0) {
            throw new GuliException(20001, "修改课程信息失败");
        }
        //修改描述表
        EduCourseDescription description = new EduCourseDescription();
        description.setId(courseInfoVo.getId());
        description.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(description);
    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }

    //删除课程(使用逻辑外键---实际开发中不使用物理外键 ，会降低数据库性能)
    //edu_course表中有is_delete逻辑删除字段，其余几个表没有，所以只有edu_course表逻辑删除，其余表物理删除
    @Override
    public void removeCourse(String courseId) {
        //根据课程id删除小节
        //不使用eduVideoService.removeById()，此方法里要传入的是video这个表的主键id，也就是小节的id，而不是课程id
        eduVideoService.removeVideoByCourseId(courseId);
        //根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);
        //根据课程id删除描述
        courseDescriptionService.removeById(courseId);
        //根据课程id删除课程本身
        int result = baseMapper.deleteById(courseId);
        if (result == 0) { //失败返回
            throw new GuliException(20001, "删除失败");
        }
    }

    //条件分页查询课程
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
        //根据讲师id查询所讲课程
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status","Normal"); //查询已发布的课程
        //判断条件值是否为空，不为空拼接
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) { //一级分类
            wrapper.eq("subject_parent_id", courseFrontVo.getSubjectParentId());
//            if (!StringUtils.isEmpty(courseFrontVo.getSubjectId())) { //二级分类
//                wrapper.eq("subject_id", courseFrontVo.getSubjectId());
//            }
            wrapper.eq(!StringUtils.isEmpty(courseFrontVo.getSubjectId()), "subject_id", courseFrontVo.getSubjectId());
        }
        if (!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) { //关注度
            wrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) { //最新
            wrapper.orderByDesc("gmt_create");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())) { //价格
            wrapper.orderByAsc("price");
        }

        baseMapper.selectPage(pageParam, wrapper);

        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext(); //下一页
        boolean hasPrevious = pageParam.hasPrevious(); //上一页

        //把分页数据获取出来，放到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        //map返回
        return map;
    }

    //课程详情
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }
}
