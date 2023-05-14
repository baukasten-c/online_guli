package atguigu.educenter.service;

import atguigu.educenter.pojo.UcenterMember;
import atguigu.educenter.pojo.vo.LoginVo;
import atguigu.educenter.pojo.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-05-07
 */
public interface UcenterMemberService extends IService<UcenterMember> {
    //登录
    String login(LoginVo loginVo);
    //注册
    void register(RegisterVo registerVo);
    //根据openid判断数据表里面是否存在相同微信信息
    UcenterMember getOpenIdMember(String openid);
    //查询某一天注册人数
    Integer countRegisterDay(String day);
}
