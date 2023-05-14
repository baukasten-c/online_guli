package atguigu.eduservice.controller;

import atguigu.commonutils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduservice/user")
//@CrossOrigin ////解决跨域
public class EduLoginController {
    //URL请求中不采用大小写混合的驼峰命名方式，尽量采用全小写单词，如果需要连接多个单词，则采用连接符“_”连接单词
    //接口路径最开头和最结尾的斜杠可加可不加，没加的话，在构造全路径的时候spring会自动加上
    //login
    @PostMapping("login")
    public R login(){
        return R.ok().data("token", "admin");
    }
    //login返回的是当前登录人的信息，info是登录进去首页面的信息
    //info
    @GetMapping("info")
    public R info(){
        return R.ok().data("name", "admin").data("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
    }
}
