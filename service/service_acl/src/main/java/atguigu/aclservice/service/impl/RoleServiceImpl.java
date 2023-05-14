package atguigu.aclservice.service.impl;

import atguigu.aclservice.mapper.RoleMapper;
import atguigu.aclservice.pojo.Role;
import atguigu.aclservice.pojo.RolePermission;
import atguigu.aclservice.pojo.UserRole;
import atguigu.aclservice.service.RoleService;
import atguigu.aclservice.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private UserRoleService userRoleService;

    //根据用户获取角色数据
    @Override
    public Map<String, Object> findRoleByUserId(String userId) {
        //查询所有的角色
        List<Role> allRolesList = baseMapper.selectList(null);

        //根据用户id，查询用户拥有的角色id
        QueryWrapper<UserRole> wrapper = new QueryWrapper<UserRole>().eq("user_id", userId);
        List<UserRole> userRoleList = userRoleService.list(wrapper);
        List<String> existRoleList = userRoleList.stream()
                .map(UserRole::getRoleId) //结果不是一个 key-value 映射，而只是一个包含权限ID的流
                .collect(Collectors.toList()); //将流中的元素收集到一个列表中，得到了一个包含所有权限ID的列表

        //对角色进行分类
        List<Role> assignRoles = new ArrayList();
        for (Role role : allRolesList) {
            if (existRoleList.contains(role.getId())) { //已分配
                assignRoles.add(role);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoles", assignRoles);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }

    //给用户分配角色
    @Override
    public void saveUserRoleRealtionShip(String userId, String[] roleIds) {
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));

        List<UserRole> userRoleList = new ArrayList<>();
        for(String roleId : roleIds) {
            if(StringUtils.isEmpty(roleId)) continue;
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleList.add(userRole);
        }
        userRoleService.saveBatch(userRoleList);
    }

    //根据用户id获取角色
    @Override
    public List<Role> selectRoleByUserId(String userId) {
        //根据用户id，查询用户拥有的角色id
        QueryWrapper<UserRole> wrapper = new QueryWrapper<UserRole>().eq("user_id", userId);
        List<UserRole> userRoleList = userRoleService.list(wrapper);
        List<String> roleIdList = userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        List<Role> roleList = new ArrayList<>();
        if(roleIdList.size() > 0) {
            roleList = baseMapper.selectBatchIds(roleIdList); //查询对应的角色列表
        }
        return roleList;
    }
}
