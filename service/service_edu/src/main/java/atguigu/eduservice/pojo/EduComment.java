package atguigu.eduservice.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 评论
 * </p>
 *
 * @author testjava
 * @since 2023-05-10
 */
@Getter
@Setter
@TableName("edu_comment")
@ApiModel(value = "EduComment对象", description = "评论")
public class EduComment implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("讲师ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("课程id")
    private String courseId;

    @ApiModelProperty("讲师id")
    private String teacherId;

    @ApiModelProperty("会员id")
    private String memberId;

    @ApiModelProperty("会员昵称")
    private String nickname;

    @ApiModelProperty("会员头像")
    private String avatar;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
