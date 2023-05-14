package atguigu.eduorder.service;

import atguigu.eduorder.pojo.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
public interface PayLogService extends IService<PayLog> {
    //微信支付二维码
    Map createNative(String orderNo);
    //查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);
    //添加支付记录和更新订单状态
    void updateOrdersStatus(Map<String, String> map);
}
