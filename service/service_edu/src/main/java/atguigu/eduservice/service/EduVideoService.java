package atguigu.eduservice.service;

import atguigu.eduservice.pojo.EduVideo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
public interface EduVideoService extends IService<EduVideo> {
    //根据课程id删除小节
    void removeVideoByCourseId(String courseId);
}
