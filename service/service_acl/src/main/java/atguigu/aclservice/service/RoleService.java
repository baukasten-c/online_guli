package atguigu.aclservice.service;


import atguigu.aclservice.pojo.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface RoleService extends IService<Role> {
    //根据用户获取角色数据
    Map<String, Object> findRoleByUserId(String userId);
    //根据用户分配角色
    void saveUserRoleRealtionShip(String userId, String[] roleId);
    //根据用户id获取角色
    List<Role> selectRoleByUserId(String userId);
}
