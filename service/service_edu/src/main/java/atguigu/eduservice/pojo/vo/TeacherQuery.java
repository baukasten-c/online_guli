package atguigu.eduservice.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

//VO:视图层对象(value object,面向前端数据展示的对象)，DTO：数据传输对象
//vo：前端传过来的数据，dto对数据进行操作，do对数据库进行操作
@Data
public class TeacherQuery {
    @ApiModelProperty(value = "教师名称,模糊查询")
    private String name;

    @ApiModelProperty(value = "头衔 1高级讲师 2首席讲师")
    private Integer level;

    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin; //注：使用String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
    private String end;
}
