package atguigu.eduservice.pojo.chapter;

import lombok.Data;

@Data
public class VideoVo {
    private String id;
    private String title;
    private Integer isFree;
    private String videoSourceId; //视频id
}
