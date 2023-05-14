package atguigu.eduservice.service.impl;

import atguigu.eduservice.pojo.EduChapter;
import atguigu.eduservice.mapper.EduChapterMapper;
import atguigu.eduservice.pojo.EduSubject;
import atguigu.eduservice.pojo.EduVideo;
import atguigu.eduservice.pojo.chapter.ChapterVo;
import atguigu.eduservice.pojo.chapter.VideoVo;
import atguigu.eduservice.pojo.subject.OneSubject;
import atguigu.eduservice.pojo.subject.TwoSubject;
import atguigu.eduservice.service.EduChapterService;
import atguigu.eduservice.service.EduVideoService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {
    @Autowired
    private EduVideoService videoService; //注入小节service
    //课程大纲列表,根据课程id进行查询
    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {
        //根据课程id查询课程里面所有的章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id", courseId);
        List<EduChapter> eduChapterList = baseMapper.selectList(wrapperChapter);

        //根据课程id查询课程里面所有的小节
        QueryWrapper<EduVideo>  wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        List<EduVideo> eduVideoList = videoService.list(wrapperVideo);

        //创建list集合，用于存储最终封装数据
        List<ChapterVo> finalChapterList = new ArrayList<>();

        //遍历查询章节list集合进行封装
        for (EduChapter eduChapter : eduChapterList) { //遍历eduChapterList集合
            ChapterVo chapterVo = new ChapterVo();
            //eduChapter对象值复制到ChapterVo里面
            BeanUtils.copyProperties(eduChapter, chapterVo);
            //封装到要求的finalChapterList集合里面
            finalChapterList.add(chapterVo);

            //创建集合，用于封装章节的小节
            List<VideoVo> videoList = new ArrayList<>();
            //遍历查询小节list集合，进行封装
            for (EduVideo eduVideo : eduVideoList) {
                //判断小节里面chapterid和章节里面id是否一样
                if (eduVideo.getChapterId().equals(eduChapter.getId())) {
                    //进行封装
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo, videoVo);
                    videoList.add(videoVo);
                }
            }
            //把封装之后小节list集合，放到章节对象里面
            //finalChapterList集合里添加的只是chapterVo这个引用，并没有重新new一个chapterVo对象，所以chapterVo值改变了，finalList里也会跟着改变
            chapterVo.setChildren(videoList);
        }
        return finalChapterList;
    }

    //删除章节
    @Override
    public boolean deleteChapter(String chapterId) {
        //根据chapterid章节id 查询小节表，如果查询数据，不进行删除
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId);
        long count = videoService.count(wrapper);
        //判断
        if(count > 0){ //查询出小节，不进行删除
            throw new GuliException(20001, "不能删除");
        }else{ //不能查询数据，进行删除
            int result = baseMapper.deleteById(chapterId); //删除章节
            return result > 0;
        }
    }

    //根据课程id删除章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        Long count = baseMapper.selectCount(wrapper);
        int delete = baseMapper.delete(wrapper);
        if(count != delete){
            throw new GuliException(20001, "章节删除失败");
        }
    }
}
