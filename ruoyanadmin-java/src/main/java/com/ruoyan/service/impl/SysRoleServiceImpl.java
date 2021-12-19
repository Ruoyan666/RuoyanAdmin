package com.ruoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.exception.CheckSuperAdminException;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysRole;
import com.ruoyan.entity.SysRoleMenu;
import com.ruoyan.entity.SysUserRole;
import com.ruoyan.mapper.SysRoleMapper;
import com.ruoyan.service.SysRoleMenuService;
import com.ruoyan.service.SysRoleService;
import com.ruoyan.service.SysUserRoleService;
import com.ruoyan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ruoyan.commom.lang.Const.*;

/**
 * @Package: com.ruoyan.service.impl
 * @ClassName: SysRoleServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:59
 * @Description: 角色服务层接口实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper,SysRole> implements SysRoleService
{
    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysRole> listRolesByUserId(Long userId)
    {
        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>()
                .inSql("id", "select role_id from sys_user_role where user_id = " + userId));

        return sysRoles;
    }

    @Override
    public void superAdminCheck(String className, String functionName)
    {
        if("com.ruoyan.controller.SysRoleController".equals(className))
        {
            if(Const.UPDATE.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色信息不允许被修改");
            }
            else if(Const.DELETE.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色不允许被删除");
            }
            else if(Const.PERM.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色不允许被修改菜单权限");
            }
        }
        else if("com.ruoyan.controller.SysUserController".equals(className))
        {
            if(Const.UPDATE.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色信息不允许被修改");
            }
            else if(Const.DELETE.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色不允许被删除");
            }
            else if(Const.ROLEPERM.equals(functionName))
            {
                throw new CheckSuperAdminException("超级管理员角色不允许被修改角色权限");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateByTransactional(SysRole sysRole)
    {
        sysRole.setUpdated(LocalDateTime.now());

        this.updateById(sysRole);

        //清除redis中与该角色所有相关的权限缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());

        return Result.success(sysRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByTransactional(Long[] roleIds)
    {
        this.removeByIds(Arrays.asList(roleIds));

        //同步删除中间表数据
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", roleIds));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", roleIds));

        //清除redis中与该角色所有相关的权限缓存
        for (Long roleId : roleIds)
        {
            sysUserService.clearUserAuthorityInfoByRoleId(roleId);
        }

        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePermissions(Long roleId, Long[] menuIds)
    {
        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();

        for (Long menuId : menuIds)
        {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(roleId);

            sysRoleMenuList.add(sysRoleMenu);
        }

        //先将原来的记录删除，再将新数据保存进去
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(sysRoleMenuList);

        //清除redis中与该角色所有相关的权限缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);

        return Result.success(menuIds);
    }
}
