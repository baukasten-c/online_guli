package atguigu.vod.service.impl;

import atguigu.servicebase.exceptionhandler.GuliException;
import atguigu.vod.Utils.ConstantVodUtils;
import atguigu.vod.Utils.InitVodCilent;
import atguigu.vod.service.VodService;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class VodServiceImpl implements VodService {
    //上传视频
    @Override
    public String uploadVideoAly(MultipartFile file) {
        //在这整个项目中，因为vue框架的原因，导致我们的文件默认变量是file，所有后端一定要用file这个变量名接收
        try {
            String accessKeyId = ConstantVodUtils.ACCESS_KEY_ID;
            String accessKeySecret = ConstantVodUtils.ACCESS_KEY_SECRET;
            //上传文件原始名称
            String fileName = file.getOriginalFilename();
            //上传之后显示名称
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            //inputStream：上传文件输入流
            InputStream inputStream = file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(accessKeyId, accessKeySecret, title, fileName, inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            String videoId = response.getVideoId();
            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                log.warn(errorMessage);
                if(StringUtils.isEmpty(videoId)){
                    throw new GuliException(20001, errorMessage);
                }
            }
            return videoId;
        }catch (Exception e) {
            throw new GuliException(20001, "guli vod 服务上传失败");
        }
    }

    //删除多个阿里云视频
    @Override
    public void removeMoreAlyVideo(List<String> videoIdList) {
        try {
            //初始化对象
            DefaultAcsClient client = InitVodCilent.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            //创建删除视频request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //videoIdList值转换成 1,2,3
//            String videoIds = StringUtils.join(videoIdList.toArray(), ",");
            String videoIds = String.join(",", videoIdList);
            //向request设置视频id
            request.setVideoIds(videoIds);
            //调用初始化对象的方法实现删除
            client.getAcsResponse(request);
        }catch(Exception e) {
            throw new GuliException(20001,"删除视频失败");
        }
    }
}
