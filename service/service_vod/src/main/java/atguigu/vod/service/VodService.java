package atguigu.vod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    //上传视频
    String uploadVideoAly(MultipartFile file);
    //删除多个阿里云视频
    void removeMoreAlyVideo(List<String> videoIdList);
}
