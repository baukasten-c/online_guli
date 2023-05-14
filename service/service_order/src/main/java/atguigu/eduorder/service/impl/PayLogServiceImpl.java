package atguigu.eduorder.service.impl;

import atguigu.eduorder.pojo.Order;
import atguigu.eduorder.pojo.PayLog;
import atguigu.eduorder.mapper.PayLogMapper;
import atguigu.eduorder.service.OrderService;
import atguigu.eduorder.service.PayLogService;
import atguigu.eduorder.utils.HttpClient;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {
    @Autowired
    private OrderService orderService;

    //微信支付二维码
    @Override
    public Map createNative(String orderNo) {
        try {
            //1.根据订单号查询订单信息
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no", orderNo);
            Order order = orderService.getOne(wrapper);

            //2.使用map设置生成二维码需要参数
            Map m = new HashMap();
            m.put("appid", "wx74862e0dfcf69954"); //公众号或小程序的唯一标识符
            m.put("mch_id", "1558950191"); //商户号，用于标识商户身份
            m.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串，用于生成签名
            m.put("body", order.getCourseTitle()); //课程标题
            m.put("out_trade_no", orderNo); //订单号
            //订单总金额，单位为分。需要将金额转换为整数形式，例如将金额乘以100并转换为字符串
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue() + "");
            m.put("spbill_create_ip", "127.0.0.1"); //发起支付请求的客户端IP地址
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n"); //接收微信支付结果通知的URL地址
            m.put("trade_type", "NATIVE"); //交易类型，指定为NATIVE表示扫码支付

            //3.HTTPClient来根据URL访问第三方接口并且传递参数(xml格式)，微信支付提供固定的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式的参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb")); //key为商户密钥
            client.setHttps(true); //设置client对象使用HTTPS协议与微信支付接口进行通信
            //执行post请求发送
            client.post();

            //4.得到发送请求返回结果(使用xml格式返回)
            String xml = client.getContent();
            //把xml格式转换map集合，把map集合返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            //5.封装返回结果集
            //resultMap是从微信支付接口返回的XML结果中解析出来的，它包含了与支付相关的信息，而不仅仅是生成二维码所需的信息
            Map map = new HashMap();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));  //返回二维码操作状态码
            map.put("code_url", resultMap.get("code_url")); //二维码地址

            return map;
        } catch (Exception e) {
            throw new GuliException(20001, "生成二维码失败");
        }
    }

    //查询订单支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1.封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2.发送httpclient
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();

            //3.得到请求返回内容
            String xml = client.getContent();

            //4.转成Map再返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //包含：nonce_str、device_info、trade_state、out_trade_no、appid、total_fee、trade_state_desc、sign、return_msg、result_code、mch_id、return_code
            return resultMap;
        } catch (Exception e) {
            throw new GuliException(20001, "查询订单支付状态失败");
        }
    }

    //添加支付记录和更新订单状态
    @Override
    public void updateOrdersStatus(Map<String, String> map) {
        //1.从map获取订单号
        String orderNo = map.get("out_trade_no");
        //2.根据订单号查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        Order order = orderService.getOne(wrapper);

        //3.更新订单表订单状态
        if (order.getStatus().intValue() == 1) {
            return;
        }
        order.setStatus(1); //1代表已经支付
        orderService.updateById(order);

        //4.向支付表添加支付记录
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);  //订单号
        payLog.setPayTime(new Date()); //订单完成时间
        payLog.setPayType(1); //支付类型 1微信
        payLog.setTotalFee(order.getTotalFee()); //总金额(分)
        payLog.setTradeState(map.get("trade_state")); //支付状态
        payLog.setTransactionId(map.get("transaction_id")); //流水号
        //将map对象转换为JSON字符串并设置到支付日志对象的属性中
        payLog.setAttr(JSONObject.toJSONString(map));

        //5.插入到支付表
        baseMapper.insert(payLog);
    }
}
