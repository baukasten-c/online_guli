package atguigu.eduservice.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseQuery {
    @ApiModelProperty(value = "课程标题")
    private String title;
    @ApiModelProperty(value = "课程状态")
    private String status;
    @ApiModelProperty(value = "课程讲师ID")
    private String teacherId;
}
