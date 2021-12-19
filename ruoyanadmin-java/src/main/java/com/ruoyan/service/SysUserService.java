package com.ruoyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.commom.dto.PasswordDto;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.commom.lang.Result;
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

    /**
     * 通过当前用户获取用户Dto信息
     *
     * @param sysUser
     * @return SysUserDto
     */
    SysUserDto getUserInfo(SysUser sysUser);

    /**
     * 利用事务对用户进行新增操作
     *
     * @param sysUser
     * @return Result
     */
    Result saveUser(SysUser sysUser);

    /**
     * 利用事务对用户进行删除操作（可批量删除）
     *
     * @param userIds
     * @return Result
     */
    Result deleteByTransactional(Long[] userIds);

    /**
     * 更新用户下的角色权限信息
     * 第一个参数为当前用户的Id
     * 第二个参数为当前用户相关联的角色Id数组
     *
     * @param userId
     * @param roleIds
     * @return Result
     */
    Result updatePermissions(Long userId, Long[] roleIds);

    /**
     * 重置用户密码接口
     *
     * @param sysUser
     * @return Result
     */
    Result resetPassword(SysUser sysUser);

    /**
     * 更新当前用户密码接口
     * 第一个参数为当前用户实体类
     * 第二个参数为密码Dto实体类
     *
     * @param sysUser
     * @param passwordDto
     * @return Result
     */
    Result updatePassword(SysUser sysUser, PasswordDto passwordDto);
}
