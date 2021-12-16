package com.ruoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysRole;
import com.ruoyan.mapper.SysRoleMapper;
import com.ruoyan.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<SysRole> listRolesByUserId(Long userId)
    {
        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>()
                .inSql("id", "select role_id from sys_user_role where user_id = " + userId));

        return sysRoles;
    }

    @Override
    public Result superAdminCheck(String className, String funtionName)
    {
        if("com.ruoyan.controller.SysRoleController".equals(className))
        {
            if("update".equals(funtionName))
            {
                return Result.fail("超级管理员角色信息不允许被修改");
            }
            else if("delete".equals(funtionName))
            {
                return Result.fail("超级管理员角色不允许被删除");
            }
            else if("perm".equals(funtionName))
            {
                return Result.fail("超级管理员角色不允许被修改菜单权限");
            }
        }
        else if("com.ruoyan.controller.SysUserController".equals(className))
        {
            if("update".equals(funtionName))
            {
                return Result.fail("超级管理员角色信息不允许被修改");
            }
            else if("delete".equals(funtionName))
            {
                return Result.fail("超级管理员角色不允许被删除");
            }
            else if("rolePerm".equals(funtionName))
            {
                return Result.fail("超级管理员角色不允许被修改角色权限");
            }
        }

        return Result.fail(" ");
    }
}
