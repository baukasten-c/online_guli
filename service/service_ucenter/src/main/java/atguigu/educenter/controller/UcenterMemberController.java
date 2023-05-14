package atguigu.educenter.controller;

import atguigu.commonutils.JwtUtils;
import atguigu.commonutils.R;
import atguigu.commonutils.ordervo.UcenterMemberOrder;
import atguigu.educenter.pojo.UcenterMember;
import atguigu.educenter.pojo.vo.LoginVo;
import atguigu.educenter.pojo.vo.RegisterVo;
import atguigu.educenter.service.UcenterMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-05-07
 */
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin
public class UcenterMemberController {
    @Autowired
    private UcenterMemberService memberService;

    //登录
    @PostMapping("login")
    public R loginUser(@RequestBody LoginVo loginVo) {
        //调用service方法实现登录
        String token = memberService.login(loginVo);
        //返回token值，使用jwt生成
        return R.ok().data("token", token);
    }

    //注册
    @PostMapping("register")
    public R registerUser(@RequestBody RegisterVo registerVo) {
        //vo不需要判断是否为空，requestBody中的require默认为true
        memberService.register(registerVo);
        return R.ok();
    }

    //根据token获取用户信息
    @GetMapping("getMemberInfo")
    public R getMemberInfo(HttpServletRequest request) {
        //调用jwt工具类的方法。根据request对象获取头信息，返回用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //查询数据库根据用户id获取用户信息
        UcenterMember member = memberService.getById(memberId);
        return R.ok().data("userInfo", member); //@RestController注解标记的类返回的数据默认是JSON格式的数据
    }

    //根据id获取用户信息(评论)
    @GetMapping("getMemberInfoById/{memberId}")
    public Map getMemberInfoById(@PathVariable String memberId) {
        //根据用户id查询用户信息
        UcenterMember ucenterMember = memberService.getById(memberId);
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", ucenterMember);
        return map;
    }

    //根据用户id获取用户信息(订单)
    @GetMapping("getUserInfoOrder/{id}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable String id) {
        UcenterMember member = memberService.getById(id);
        //把member对象里面值复制给UcenterMemberOrder对象
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member, ucenterMemberOrder);
        return ucenterMemberOrder;
    }

    //查询某一天注册人数
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day) {
        Integer count = memberService.countRegisterDay(day);
        return R.ok().data("countRegister", count);
    }
}
