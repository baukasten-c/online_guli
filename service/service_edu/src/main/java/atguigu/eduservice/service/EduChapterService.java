package atguigu.eduservice.service;

import atguigu.eduservice.pojo.EduChapter;
import atguigu.eduservice.pojo.chapter.ChapterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
public interface EduChapterService extends IService<EduChapter> {
    //课程大纲列表,根据课程id进行查询
    List<ChapterVo> getChapterVideoByCourseId(String courseId);
    //删除章节
    boolean deleteChapter(String chapterId);
    //根据课程id删除章节
    void removeChapterByCourseId(String courseId);
}
