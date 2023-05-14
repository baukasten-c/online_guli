package atguigu.eduorder.controller;

import atguigu.commonutils.R;
import atguigu.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
@RestController
@RequestMapping("/eduorder/paylog")
//@CrossOrigin
public class PayLogController {
    @Autowired
    private PayLogService payLogService;

    //生成微信支付二维码接口(参数是订单号)
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        //这里的二维码是包含订单号生成的，每个二维码对应一个订单号，支付之后又可以实时回调而查询订单情况
        //返回信息，包含二维码地址，还有其他需要的信息
        Map map = payLogService.createNative(orderNo);
        return R.ok().data(map);
    }

    //根据订单号查询查询订单支付状态(参数：订单号)
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        if(map == null) {
            return R.error().message("支付出错了");
        }
        //如果返回map里面不为空，通过map获取订单状态
        //SUCCESS--支付成功、REFUND--转入退款、NOTPAY--未支付、CLOSED--已关闭、REVOKED--已撤销(刷卡支付)、USERPAYING--用户支付中
        if(map.get("trade_state").equals("SUCCESS")) { //支付成功
            //添加记录到支付表，更新订单表订单状态
            payLogService.updateOrdersStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");
    }
}
