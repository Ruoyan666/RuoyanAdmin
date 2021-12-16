package com.ruoyan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysRole;

import java.util.List;

/**
 * @Package: com.ruoyan.service
 * @ClassName: SysRoleService
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:56
 * @Description: 角色服务层接口
 */
public interface SysRoleService extends IService<SysRole>
{
    /**
     * 通过用户Id查询用户的角色信息，并返回一个角色信息列表
     *
     * @param userId
     * @return List<SysRole>
     */
    List<SysRole> listRolesByUserId(Long userId);

    /**
     * 检查超级管理员身份并根据不同方法名返回不能操作超级管理员的提示信息
     * 第一个参数为操作超级管理员的方法名
     *
     * @param className
     * @param funtionName
     * @return Result
     */
    public Result superAdminCheck(String className,String funtionName);
}
