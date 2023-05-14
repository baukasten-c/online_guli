package atguigu.eduservice.service.impl;

import atguigu.eduservice.client.VodClient;
import atguigu.eduservice.pojo.EduVideo;
import atguigu.eduservice.mapper.EduVideoMapper;
import atguigu.eduservice.service.EduVideoService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {
    @Autowired
    private VodClient vodClient;
    //根据课程id删除小节
    @Override
    public void removeVideoByCourseId(String courseId) {
        //删除课程内所有视频
        //根据课程id查询课程所有的视频id
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        wrapperVideo.isNotNull("video_source_id");
        wrapperVideo.select("video_source_id");
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);
        // List<EduVideo>变成List<String>
        List<String> videoIds = eduVideoList.stream()
                .map(EduVideo::getVideoSourceId)  //将每个 EduVideo 对象转换为 videoSourceId
                .collect(Collectors.toList());    //收集结果为 List<String>
//        List<String> videoIds = new ArrayList<>();
//        for (int i = 0; i < eduVideoList.size(); i++) {
//            EduVideo eduVideo = eduVideoList.get(i);
//            String videoSourceId = eduVideo.getVideoSourceId();
//            if (!StringUtils.isEmpty(videoSourceId)) {
//                //放到videoIds集合里面
//                videoIds.add(videoSourceId);
//            }
//        }
        //根据多个视频id删除多个视频
        if (videoIds.size() > 0) {
            vodClient.deleteBatch(videoIds);
        }
        //删除课程内所有小节
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        Long count = baseMapper.selectCount(wrapper);
        int delete = baseMapper.delete(wrapper);
        if (count != delete) {
            throw new GuliException(20001, "小节删除失败");
        }
    }
}
