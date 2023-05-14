package atguigu.aclservice.mapper;


import atguigu.aclservice.pojo.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    //查询所有权限菜单名
    List<String> selectAllPermissionValue();
    //根据用户id查询用户具有的权限菜单名列表
    List<String> selectPermissionValueByUserId(String userId);
    //根据用户id查询用户具有的权限菜单列表
    List<Permission> selectPermissionByUserId(String userId);
}
