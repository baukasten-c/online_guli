package atguigu.educenter.controller;

import atguigu.commonutils.JwtUtils;
import atguigu.educenter.pojo.UcenterMember;
import atguigu.educenter.service.UcenterMemberService;
import atguigu.educenter.utils.ConstantWxUtils;
import atguigu.educenter.utils.HttpClientUtils;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.util.HashMap;

@Controller //只是请求地址，不需要返回数据，所以不用@RestController(否则返回字符串地址)
@RequestMapping("/api/ucenter/wx")
//@CrossOrigin
public class WxApiController {
    @Autowired
    private UcenterMemberService memberService;

    //生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode() {
        //固定地址，后面拼接参数
//        String url = "https://open.weixin.qq.com/" + "connect/qrconnect?appid=" + ConstantWxUtils.WX_OPEN_APP_ID + "&response_type=code";

        //微信开放平台授权baseUrl  %s相当于?代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //回调地址，获取业务服务器重定向地址(由二维码界面到首页)
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            //对redirectUrl进行URLEncoder编码(参数redirect_uri的要求)
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        } catch (Exception e) {
            throw new GuliException(20001, e.getMessage());
        }
        //设置%s里面值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID, //appid
                redirectUrl, //redirect_uri
                "atguigu" //state
        );
        //重定向到请求微信地址里面
        return "redirect:" + url;
    }

    //获取扫描人信息，添加数据
    @GetMapping("callback")
    public String callback(String code, String state) {
        //1.获取code值(临时票据)，类似于验证码
        //2.拿着code请求向微信固定的地址发送请求，得到两个值 accsess_token 和 openid
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        //拼接三个参数 ：id、秘钥、code值
        String accessTokenUrl = String.format(
                baseAccessTokenUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_APP_SECRET,
                code
        );
        //请求这个拼接好的地址，得到返回的accessTokenInfo字符串(其中包含accsess_token和openid)
        String accessTokenInfo = null;
        try {
            //使用httpclient发送请求，得到返回结果
            accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new GuliException(20001, "获取access_token失败");
        }
        //从accessTokenInfo字符串获取出来两个值 accsess_token 和 openid
        //使用json转换工具Gson来解析json字符串
        Gson gson = new Gson();
        //把accessTokenInfo字符串转换map集合，根据map里面key获取对应值
        HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
        //access_token是访问用户微信资源的凭证
        String access_token = (String) mapAccessToken.get("access_token");
        //openid是每个用户在同一应用中唯一的标识符(基于微信公众平台或者开放平台的 OAuth2.0 机制生成)
        String openid = (String) mapAccessToken.get("openid");
        //3.查询数据库当前用用户是否曾经使用过微信登录
        //判断数据表里面是否存在相同微信信息，根据openid判断
        UcenterMember member = memberService.getOpenIdMember(openid);
        if (member == null) { //memeber是空，表中没有相同微信数据，把扫码人信息添加数据库里面(扫码是注册并登录)
            //4.拿着得到accsess_token和openid，再去向微信提供固定的地址发送请求，获取到扫码人信息
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            //拼接两个参数
            String userInfoUrl = String.format(
                    baseUserInfoUrl,
                    access_token,
                    openid
            );
            //发送请求
            String userInfo = null;
            try {
                userInfo = HttpClientUtils.get(userInfoUrl);
                //其中包含openid、nickname(微信昵称)、sex(1男，2女，0未知)、language、city、province、country、headimgurl、privilege、unionid
            } catch (Exception e) {
                throw new GuliException(20001, "获取用户信息失败");
            }
            //解析json，获取返回userinfo字符串中扫码人信息
            HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
            String nickname = (String) userInfoMap.get("nickname"); //昵称
            String headimgurl = (String) userInfoMap.get("headimgurl"); //头像
            //5.向数据库中插入一条记录
            member = new UcenterMember();
            //不需要id，mybatisPlus生成唯一id
            member.setOpenid(openid);
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            memberService.save(member);
        }
        //6.使用jwt根据member对象生成token字符串
        String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
        //7.登录：返回首页面，通过路径传递token字符串
        //因为端口号不同存在跨域问题，cookie不能跨域，所以这里使用url重写
        //单点登录可以将token放到cookie中，不用重写的原因：之前是后端和前端交互，现在是后端和浏览器交互，前端自然拿不到cookie
        return "redirect:http://localhost:3000?token=" + jwtToken;
    }
}
