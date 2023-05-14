package atguigu.educms.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 首页banner表
 * </p>
 *
 * @author testjava
 * @since 2023-05-04
 */
@Data
@TableName("crm_banner")
@ApiModel(value = "CrmBanner对象", description = "首页banner表")
public class CrmBanner implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("图片地址")
    private String imageUrl;

    @ApiModelProperty("链接地址")
    private String linkUrl;

    @ApiModelProperty("排序")
    private Integer sort;

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
