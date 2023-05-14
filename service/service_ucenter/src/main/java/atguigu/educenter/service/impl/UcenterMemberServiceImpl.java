package atguigu.educenter.service.impl;

import atguigu.commonutils.JwtUtils;
import atguigu.commonutils.MD5;
import atguigu.educenter.pojo.UcenterMember;
import atguigu.educenter.mapper.UcenterMemberMapper;
import atguigu.educenter.pojo.vo.LoginVo;
import atguigu.educenter.pojo.vo.RegisterVo;
import atguigu.educenter.service.UcenterMemberService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-05-07
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //登录
    @Override
    public String login(LoginVo loginVo) {
        //获取登录手机号和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //手机号和密码非空判断
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001, "登录失败");
        }
        //根据手机号获取用户信息
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if (mobileMember == null) { //没有这个手机号
            throw new GuliException(20001, "手机号未注册，登录失败");
        }
        //判断用户是否禁用
        if (mobileMember.getIsDisabled()) {
            throw new GuliException(20001, "用户被禁用，登录失败");
        }
        //判断密码
        //存储到数据库的密码都加密过(加密方式 MD5)，所以把输入的密码进行加密再和数据库密码进行比较
        if (!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new GuliException(20001, "密码错误，登录失败");
        }
        //登录成功(不需要缓存，只要携带token就认为已经登录了)
        //生成token字符串，使用jwt工具类
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return jwtToken;
    }

    //注册
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String mobile = registerVo.getMobile(); //手机号
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码
        //非空判断
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)) {
            throw new GuliException(20001, "注册失败");
        }
        //判断验证码
        String redisCode = redisTemplate.opsForValue().get(mobile); //获取redis验证码
        if (!code.equals(redisCode)) {
            throw new GuliException(20001, "验证码错误，注册失败");
        }
        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GuliException(20001, "手机号已注册，注册失败");
        }
        //数据添加数据库中
        UcenterMember member = new UcenterMember();
        member.setMobile(mobile);
        member.setNickname(nickname);
        member.setIsDisabled(false); //用户不禁用
        member.setPassword(MD5.encrypt(password)); //密码需要加密的
        member.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        baseMapper.insert(member);
    }

    //根据openid判断数据表里面是否存在相同微信信息
    @Override
    public UcenterMember getOpenIdMember(String openid) {
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }

    //查询某一天注册人数
    @Override
    public Integer countRegisterDay(String day) {
        return baseMapper.countRegisterDay(day);
    }
}
