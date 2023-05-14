package atguigu.staservice.pojo;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 网站统计日数据
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
@Getter
@Setter
@TableName("statistics_daily")
@ApiModel(value = "StatisticsDaily对象", description = "网站统计日数据")
public class StatisticsDaily implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("统计日期")
    private String dateCalculated;

    @ApiModelProperty("注册人数")
    private Integer registerNum;

    @ApiModelProperty("登录人数")
    private Integer loginNum;

    @ApiModelProperty("每日播放视频数")
    private Integer videoViewNum;

    @ApiModelProperty("每日新增课程数")
    private Integer courseNum;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
