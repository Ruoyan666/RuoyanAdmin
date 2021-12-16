package com.ruoyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.entity.SysUser;

/**
 * @Package: com.ruoyan.service
 * @ClassName: SysUserService
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 9:31
 * @Description: 用户服务层接口
 */
public interface SysUserService extends IService<SysUser>
{
    /**
     * 通过指定用户名查询用户
     *
     * @param username
     * @return SysUser
     */
    SysUser getByUsername(String username);

    /**
     * 通过指定用户Id查询单一用户的权限信息，权限信息包括角色编码，菜单操作编码信息
     *
     * @param userId
     * @return String(authority)
     */
    String getUserAuthorityInfo(Long userId);

    /**
     * 通过指定用户名清除用户权限信息
     *
     * @param username
     */
    void clearUserAuthorityInfo(String username);

    /**
     * 通过指定角色Id值清除用户权限信息
     *
     * @param roleId
     */
    void clearUserAuthorityInfoByRoleId(Long roleId);

    /**
     * 通过指定菜单Id值清除用户权限信息
     *
     * @param menuId
     */
    void clearUserAuthorityInfoByMenuId(Long menuId);

    SysUserDto getUserInfo(SysUser sysUser);
}
