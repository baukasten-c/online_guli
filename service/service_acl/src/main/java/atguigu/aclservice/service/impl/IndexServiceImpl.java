package atguigu.aclservice.service.impl;

import atguigu.aclservice.pojo.Role;
import atguigu.aclservice.pojo.User;
import atguigu.aclservice.service.IndexService;
import atguigu.aclservice.service.PermissionService;
import atguigu.aclservice.service.RoleService;
import atguigu.aclservice.service.UserService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RedisTemplate redisTemplate;

    //根据用户名获取用户登录信息
    public Map<String, Object> getUserInfo(String username) {
        //根据用户名查询用户
        User user = userService.selectByUsername(username);
        if (null == user) {
            throw new GuliException(20001, "获取用户登录信息失败");
        }

        //根据用户id获取角色
        List<Role> roleList = roleService.selectRoleByUserId(user.getId());
        //将角色名字封装到roleNameList中
        List<String> roleNameList = roleList.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        //前端框架必须返回一个角色，否则报错，如果没有角色，返回一个空角色
        if(roleNameList.size() == 0) {
            roleNameList.add("");
        }

        //根据用户id获取用户菜单名
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());
        //将用户名和对应的菜单列表存储到 Redis 中
        redisTemplate.opsForValue().set(username, permissionValueList);

        //将用户信息封装到result中
        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getUsername());
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("roles", roleNameList);
        result.put("permissionValueList", permissionValueList);
        return result;
    }

    //根据用户名获取动态菜单
    public List<JSONObject> getMenu(String username) {
        //根据用户名查询用户
        User user = userService.selectByUsername(username);
        //根据用户id获取用户菜单权限
        List<JSONObject> permissionList = permissionService.selectPermissionByUserId(user.getId());
        return permissionList;
    }
}
