package atguigu.eduorder.service;

import atguigu.eduorder.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
public interface OrderService extends IService<Order> {
    //创建订单，返回订单号
    String createOrders(String courseId, String memberIdByJwtToken);
}
