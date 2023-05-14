package atguigu.msmservice.controller;

import atguigu.commonutils.R;
import atguigu.msmservice.service.MsmService;
import atguigu.msmservice.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/edumsm/msm")
//@CrossOrigin
//没有对应的数据库表格，不用代码生成器
public class MsmController {
    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //发送短信
    @GetMapping("send/{phone}")
    public R sendMsm(@PathVariable String phone) {
        //从redis获取验证码，如果获取到直接返回(使五分钟之内获取同一个验证码，防止重复发送)
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return R.ok();
        }
        //如果redis获取不到，进行阿里云发送
        code = RandomUtil.getFourBitRandom(); //生成随机值，传递阿里云进行发送
        //使用map更灵活，以后想加参数了直接往map中加，不需要改方法的参数，且send()方法里需要用map转json
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        //调用service发送短信的方法
        boolean isSend = msmService.send(param, phone);
        if (isSend) {
            //发送成功，把发送成功验证码放到redis里面
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES); //设置有效时间
            return R.ok();
        } else {
            return R.error().message("短信发送失败");
        }
    }
}
