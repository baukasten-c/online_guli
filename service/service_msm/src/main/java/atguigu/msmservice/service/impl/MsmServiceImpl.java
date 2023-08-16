package atguigu.msmservice.service.impl;

import atguigu.msmservice.service.MsmService;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {
    //发送短信
    @Override
    public boolean send(Map<String, Object> param, String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        DefaultProfile profile = DefaultProfile.getProfile("default", "", "");
        IAcsClient client = new DefaultAcsClient(profile);

        //设置相关固定的参数
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");

        //设置发送相关的参数
        request.putQueryParameter("PhoneNumbers", phone); //手机号
        request.putQueryParameter("SignName", "阿里云短信测试"); //申请的阿里云签名名称
        request.putQueryParameter("TemplateCode", "SMS_154950909"); //申请的阿里云模板code
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param)); //验证码数据，转换json数据传
        try {
            //最终发送
            CommonResponse response = client.getCommonResponse(request);
            boolean success = response.getHttpResponse().isSuccess();
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
