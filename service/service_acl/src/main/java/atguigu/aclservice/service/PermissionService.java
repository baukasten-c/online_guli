package atguigu.aclservice.service;

import atguigu.aclservice.pojo.Permission;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface PermissionService extends IService<Permission> {
    //递归删除菜单
    void removeChildById(String id);
    //获取全部菜单
    Permission queryAllMenu();
    //根据角色获取菜单
    Permission selectAllMenu(String roleId);
    //给角色分配权限
    void saveRolePermissionRealtionShip(String roleId, String[] permissionId);
    //根据用户id获取用户菜单名
    List<String> selectPermissionValueByUserId(String userId);
    //根据用户id获取用户菜单权限
    List<JSONObject> selectPermissionByUserId(String userId);
}
