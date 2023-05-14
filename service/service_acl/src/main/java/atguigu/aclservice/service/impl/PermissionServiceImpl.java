package atguigu.aclservice.service.impl;

import atguigu.aclservice.utils.MemuHelper;
import atguigu.aclservice.mapper.PermissionMapper;
import atguigu.aclservice.pojo.Permission;
import atguigu.aclservice.pojo.RolePermission;
import atguigu.aclservice.pojo.User;
import atguigu.aclservice.service.PermissionService;
import atguigu.aclservice.service.RolePermissionService;
import atguigu.aclservice.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserService userService;

    //递归删除菜单
    @Override
    public void removeChildById(String id) {
        //创建list集合，用于封装所有要删除的菜单id值
        List<String> idList = new ArrayList<>();
        //添加当前菜单id到idList中
        idList.add(id);
        //查询并添加子菜单id到idList中
        this.selectPermissionChildById(id, idList);
        baseMapper.deleteBatchIds(idList);
    }

    //根据当前菜单id，递归查询菜单里的子菜单id，并添加到idList中
    private void selectPermissionChildById(String pid, List<String> idList) {
        //查询菜单里面子菜单id
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", pid);
        wrapper.select("id");
        List<Permission> childIdList = baseMapper.selectList(wrapper);
        //把childIdList里的菜单id值获取出来，封装idList里面，做递归查询
        childIdList.stream().forEach(item -> {
            //封装idList里面
            idList.add(item.getId());
            //递归查询
            this.selectPermissionChildById(item.getId(), idList);
        });
    }

    //获取全部菜单
    @Override
    public Permission queryAllMenu() {
        //查询菜单表所有数据
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        List<Permission> permissionList = baseMapper.selectList(wrapper);
        //把查询的所有菜单list集合按照要求进行
        Permission result = build(permissionList);
        return result;
    }

    //封装菜单集合
    private static Permission build(List<Permission> permissionList) {
        //遍历菜单集合
        for (Permission permissionNode : permissionList) {
            //得到顶层菜单(pid=0)
            if ("0".equals(permissionNode.getPid())) {
                //设置顶层菜单的level是1
                permissionNode.setLevel(1);
                //根据顶层菜单，向里面进行查询子菜单，封装到permissionNode里面
                findChildren(permissionNode, permissionList);
                return permissionNode;
            }
        }
        return null;
    }

    //递归查找子节点
    private static void findChildren(Permission parentNode, List<Permission> permissionList) {
        List<Permission> children = new ArrayList<>();
        //遍历菜单集合
        for (Permission permissionNode : permissionList) {
            //找到当前节点的子菜单(判断id和pid值是否相同)
            if (permissionNode.getPid().equals(parentNode.getId())) {
                //设置子菜单的级别为父菜单级别+1
                permissionNode.setLevel(parentNode.getLevel() + 1);
                //递归处理子菜单的子菜单
                findChildren(permissionNode, permissionList);
                children.add(permissionNode);
            }
        }
        // 将子菜单设置到父菜单中
        parentNode.setChildren(children);
        //没少停止条件，这里是遍历全部数据，当数据遍历完成，递归也就结束了
    }

    //根据角色获取菜单
    @Override
    public Permission selectAllMenu(String roleId) {
        //查询菜单表所有数据
        //orderByAsc方法表示按照指定的字段升序排序，而CAST(id AS SIGNED)是将菜单ID字段进行强制类型转换为有符号整数
        QueryWrapper<Permission> permissionWrapper = new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)");
        List<Permission> allPermissionList = baseMapper.selectList(permissionWrapper);

        //根据角色id获取角色权限
        QueryWrapper<RolePermission> roleWrapper = new QueryWrapper<RolePermission>().eq("role_id", roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(roleWrapper);

        //将角色权限的id值封装到permissionIdList里
        List<String> permissionIdList = rolePermissionList.stream()
                .map(RolePermission::getPermissionId) //结果不是一个 key-value 映射，而只是一个包含权限ID的流
                .collect(Collectors.toList()); //将流中的元素收集到一个列表中，得到了一个包含所有权限ID的列表

        //遍历所有权限列表，设置是否选中的标志
        allPermissionList.forEach(permission ->
                permission.setSelect(permissionIdList.contains(permission.getId())));
        Permission permissionList = build(allPermissionList);
        return permissionList;
    }

    //给角色分配权限
    @Override
    public void saveRolePermissionRealtionShip(String roleId, String[] permissionIds) { //roleId角色id，permissionIds菜单id(数组形式)
        //权限会重复添加，需要先删除再添加
        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        //创建list集合，用于封装添加数据
        List<RolePermission> rolePermissionList = new ArrayList<>();
        //遍历菜单id数组
        for(String permissionId : permissionIds) {
            if(StringUtils.isEmpty(permissionId)) continue;
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            ////封装到list集合
            rolePermissionList.add(rolePermission);
        }
        //批量添加到角色菜单关系表
        rolePermissionService.saveBatch(rolePermissionList);
    }

    //根据用户id获取用户菜单名
    @Override
    public List<String> selectPermissionValueByUserId(String userId) {
        if(this.isSysAdmin(userId)) { //如果是系统管理员，获取所有权限菜单名
            return baseMapper.selectAllPermissionValue();
        } else { //否则根据用户id查询用户具有的权限菜单名列表
            return baseMapper.selectPermissionValueByUserId(userId);
        }
    }

    //判断用户是否系统管理员
    private boolean isSysAdmin(String userId) {
        User user = userService.getById(userId);
        return null != user && "admin".equals(user.getUsername());
    }

    //根据用户id获取用户菜单
    @Override
    public List<JSONObject> selectPermissionByUserId(String userId) {
        List<Permission> selectPermissionList = null;
        if(this.isSysAdmin(userId)) { //如果是超级管理员，获取所有菜单
            selectPermissionList = baseMapper.selectList(null);
        } else { //否则根据用户id查询用户具有的权限菜单列表
            selectPermissionList = baseMapper.selectPermissionByUserId(userId);
            //检查是否存在id为1的权限，如果不存在则添加到列表中
            boolean flag = selectPermissionList.stream().anyMatch(p -> "1".equals(p.getId()));
            if (!flag) {
                Permission permissionOne = baseMapper.selectById(1);
                if (permissionOne != null) {
                    selectPermissionList.add(permissionOne);
                }
            }
        }
        Permission permissionList = build(selectPermissionList);
        List<JSONObject> result = MemuHelper.build(permissionList);
        return result;
    }
}
