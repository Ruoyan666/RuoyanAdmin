package com.ruoyan.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyan.commom.dto.PasswordDto;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysRole;
import com.ruoyan.entity.SysUser;
import com.ruoyan.entity.SysUserRole;
import com.ruoyan.security.JwtLogoutSuccessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Package: com.ruoyan.controller
 * @ClassName: SysUserController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:52
 * @Description: 用户管理控制器
 */
@Api(tags = "用户管理控制层", value = "用户Controller")
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController
{
    private final String className = this.getClass().getName();

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    /**
     * 根据用户Id获取用户信息
     *
     * @param userId
     * @return Result
     */
    @ApiOperation(value = "根据用户Id获取用户信息接口")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    @GetMapping("/info/{id}")
    public Result info(@ApiParam(value = "用户Id") @PathVariable(name = "id") Long userId)
    {
        SysUser sysUser = sysUserService.getById(userId);
        Assert.notNull(sysUser, "找不到该管理员");

        //查出用户所拥有的角色信息，并将角色信息存入用户信息中
        List<SysRole> sysRoleList = sysRoleService.listRolesByUserId(userId);
        sysUser.setSysRoles(sysRoleList);

        return Result.success(sysUser);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return Result
     */
    @ApiOperation(value = "根据用户名查询用户信息接口")
    @GetMapping("/list")
    @ApiImplicitParam(value = "用户名(可为空)", name = "roleName", dataTypeClass = String.class)
    public Result list(String username)
    {
        Page<SysUser> sysUserPageData = sysUserService.page(getPage(),
                new QueryWrapper<SysUser>().like(StringUtils.isNotBlank(username), "username", username));

        //遍历每个用户，查出用户所拥有的角色信息并其存入用户中
        for (SysUser sysUser : sysUserPageData.getRecords())
        {
            sysUser.setSysRoles(sysRoleService.listRolesByUserId(sysUser.getId()));
        }

        return Result.success(sysUserPageData);
    }

    /**
     * 新增用户项
     *
     * @param sysUser
     * @return Result
     */
    @ApiOperation(value = "新增用户项接口")
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("/save")
    @Transactional
    public Result save(@ApiParam(value = "需新增实体类信息") @Validated @RequestBody SysUser sysUser)
    {
        sysUser.setCreated(LocalDateTime.now());

        //根据默认普通用户身份码查询出角色信息
        SysUserRole sysUserRole = new SysUserRole();
        SysRole sysRole = sysRoleService.getOne(new QueryWrapper<SysRole>().eq("name", Const.DEFAULT_ROLENAME));

        //设置默认用户状态、密码、头像
        sysUser.setStatu(Const.STATUS_ON);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setAvatar(Const.DEFAULT_AVATAR);

        sysUserService.save(sysUser);

        //设置新建用户默认为普通用户
        sysUserRole.setRoleId(sysRole.getId());
        sysUserRole.setUserId(sysUser.getId());
        sysUserRoleService.save(sysUserRole);

        return Result.success(sysUser);
    }

    /**
     * 更新用户项信息
     *
     * @param sysUser
     * @return Result
     */
    @ApiOperation(value = "更新用户项信息接口")
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@ApiParam(value = "需更新实体类信息") @Validated @RequestBody SysUser sysUser)
    {
        List<SysUserRole> userRoleList = sysUserRoleService.list(
                new QueryWrapper<SysUserRole>().eq("user_id", sysUser.getId()));

        for (SysUserRole sysUserRole : userRoleList)
        {
            SysRole sysRole = sysRoleService.getById(sysUserRole.getRoleId());

            if("admin".equals(sysRole.getCode()))
            {
                String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();

                return sysRoleService.superAdminCheck(this.className, functionName);
            }
        }

        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.success(sysUser);
    }

    /**
     * 根据指定的一组用户Id（有可能只有一个用户Id，有可能有多个用户Id）
     * 若只有一个用户Id，则只删除一个用户项
     * 若有多个用户Id，则为批量删除，可删除多个用户项
     * 拥有超级管理员角色的用户不可被删除
     *
     * @param userIds
     * @return Result
     */
    @ApiOperation(value = "删除用户项信息接口(可批量删除)")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    @Transactional
    public Result delete(@ApiParam(value = "用户Id数组") @RequestBody Long[] userIds)
    {
        for (Long userId : userIds)
        {
            List<SysRole> sysRoleList = sysRoleService.listRolesByUserId(userId);

            for (SysRole sysRole : sysRoleList)
            {
                //若当前用户拥有超级管理员角色信息，则该用户不可被删除
                if("admin".equals(sysRole.getCode()))
                {
                    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();

                    return sysRoleService.superAdminCheck(className, functionName);
                }
            }
        }

        sysUserService.removeByIds(Arrays.asList(userIds));

        //删除中间表信息
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", userIds));

        return Result.success("删除成功");
    }

    /**
     * 根据用户Id和用户相关联角色Id获取用户角色信息，并可以对用户的角色信息进行修改
     *
     * @param userId
     * @param roleIds
     * @return Result
     */
    @ApiOperation(value = "更新用户角色信息接口")
    @PreAuthorize("hasAuthority('sys:user:role')")
    @PostMapping("/role/{userId}")
    @Transactional
    public Result rolePerm(@ApiParam(value = "用户Id") @PathVariable("userId") Long userId, @ApiParam(value = "与当前用户相关联的角色Id数组") @RequestBody Long[] roleIds)
    {
        for (Long roleId : roleIds)
        {
            SysRole sysRole = sysRoleService.getById(roleId);

            if("admin".equals(sysRole.getCode()))
            {
                String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();

                return sysRoleService.superAdminCheck(this.className, functionName);
            }
        }

        //List用于存储用户关联的角色数组信息
        List<SysUserRole> sysUserRoleList = new ArrayList<>();

        for (Long roleId : roleIds)
        {
            //将用户角色相关联表信息更新，将用户Id和角色Id添加到用户角色实体类中
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(userId);

            sysUserRoleList.add(sysUserRole);
        }

        //先删除中间表当前用户的角色数据
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", userId));

        //再将新更新的角色信息添加到中间表
        sysUserRoleService.saveBatch(sysUserRoleList);

        //将用户表中的旧用户数据缓存清除
        sysUserService.clearUserAuthorityInfo(sysUserService.getById(userId).getUsername());

        return Result.success(sysUserRoleList);
    }

    /**
     * 重置用户密码，初始化为maxwell
     *
     * @param userId
     * @return Result
     */
    @ApiOperation(value = "重置用户密码接口")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/repassword")
    public Result resetPassword(@ApiParam(value = "用户Id") @RequestBody Long userId)
    {
        SysUser sysUser = sysUserService.getById(userId);

        //设置默认加密密码和更新日期
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.success("OK");
    }

    @ApiOperation(value = "用户修改密码接口")
    @PostMapping("/updatePassword")
    public Result updatePassword(@ApiParam(value = "密码DTO类") @Validated @RequestBody PasswordDto passwordDto, @ApiParam(value = "当前用户名") Principal principal)
    {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        boolean matches = bCryptPasswordEncoder.matches(passwordDto.getCurrentPassword(), sysUser.getPassword());
        if(!matches)
        {
            return Result.fail("旧密码输入错误");
        }

        sysUser.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.success("OK");
    }

}
