package atguigu.eduorder.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 支付日志表
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
@Getter
@Setter
@TableName("t_pay_log")
@ApiModel(value = "PayLog对象", description = "支付日志表")
public class PayLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("支付完成时间")
    private Date payTime;

    @ApiModelProperty("支付金额（分）")
    private BigDecimal totalFee;

    @ApiModelProperty("交易流水号")
    private String transactionId;

    @ApiModelProperty("交易状态")
    private String tradeState;

    @ApiModelProperty("支付类型（1：微信 2：支付宝）")
    private Integer payType;

    @ApiModelProperty("其他属性")
    private String attr;

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
