package com.ruoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.dto.PasswordDto;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.entity.SysRole;
import com.ruoyan.entity.SysUser;
import com.ruoyan.entity.SysUserRole;
import com.ruoyan.mapper.SysUserMapper;
import com.ruoyan.service.SysMenuService;
import com.ruoyan.service.SysRoleService;
import com.ruoyan.service.SysUserRoleService;
import com.ruoyan.service.SysUserService;
import com.ruoyan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Package: com.ruoyan.service.impl
 * @ClassName: SysUserServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 9:36
 * @Description: 用户服务层接口实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService
{
    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public SysUser getByUsername(String username)
    {
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }


    /**
     * 根据指定用户Id获取用户所有权限信息，并添加到字符串
     * 返回权限字符串
     *
     * @param userId
     * @return String
     */
    @Override
    public String getUserAuthorityInfo(Long userId)
    {
        SysUser sysUser = sysUserMapper.selectById(userId);

        //权限字符串，存放用户角色编码和菜单操作编码
        String authority = "";

        //若redis缓存中存有权限数据，则直接取出权限数据即可
        if(redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername()))
        {
            authority = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());
        }
        else
        {
            //获取角色编码
            List<SysRole> sysRoleList = sysRoleService.list(new QueryWrapper<SysRole>()
                    .inSql("id", "select role_id from sys_user_role" + " where user_id = " + userId));

            //若该用户存在角色编码，则将角色编码经过字符串处理后拼接到权限字符串中
            if(sysRoleList.size() > 0)
            {
                String roleCodes = sysRoleList.stream().map(role -> "ROLE_" + role.getCode()).collect(Collectors.joining(","));

                authority = roleCodes.concat(",");
            }

            //获取菜单操作编码
            List<Long> navMenuIds = sysUserMapper.getNavMenuIds(userId);

            //若该用户存在菜单操作编码，则将菜单操作编码进行字符串处理后拼接到权限字符串
            if(navMenuIds.size() > 0)
            {
                List<SysMenu> sysMenuList = sysMenuService.listByIds(navMenuIds);

                String menuPerms = sysMenuList.stream().map(menu -> menu.getPerms()).collect(Collectors.joining(","));

                authority += authority.concat(menuPerms);
            }

            redisUtil.set("GrantedAuthority:" + sysUser.getUsername(), authority, 60 * 60);

            System.out.println(authority);
        }

        return authority;
    }

    /**
     * 根据用户名清除redis中用户权限信息
     *
     * @param username
     */
    @Override
    public void clearUserAuthorityInfo(String username)
    {
        redisUtil.del("GrantedAuthority:" + username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId)
    {
        List<SysUser> sysUserList = this.list(new QueryWrapper<SysUser>()
                .inSql("id", "select user_id from sys_user_role where" + " role_id = " + roleId));

        for (SysUser sysUser : sysUserList)
        {
            this.clearUserAuthorityInfo(sysUser.getUsername());
        }
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId)
    {
        List<SysUser> sysUserList = sysUserMapper.listByMenuId(menuId);

        for (SysUser sysUser : sysUserList)
        {
            this.clearUserAuthorityInfo(sysUser.getUsername());
        }
    }

    @Override
    public SysUserDto getUserInfo(SysUser sysUser)
    {
        SysUserDto sysUserDto = new SysUserDto();

        sysUserDto.setId(sysUser.getId());
        sysUserDto.setUsername(sysUser.getUsername());
        sysUserDto.setAvatar(sysUser.getAvatar());
        sysUserDto.setCreated(sysUser.getCreated());

        return sysUserDto;
    }

    @Override
    public Result saveUser(SysUser sysUser)
    {
        sysUser.setCreated(LocalDateTime.now());

        //根据默认普通用户身份码查询出角色信息
        SysUserRole sysUserRole = new SysUserRole();
        SysRole sysRole = sysRoleService.getOne(new QueryWrapper<SysRole>().eq("name", Const.DEFAULT_ROLENAME));

        //设置默认用户状态、密码、头像
        sysUser.setStatu(Const.STATUS_ON);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setAvatar(Const.DEFAULT_AVATAR);

        this.save(sysUser);

        //设置新建用户默认为普通用户
        sysUserRole.setRoleId(sysRole.getId());
        sysUserRole.setUserId(sysUser.getId());
        sysUserRoleService.save(sysUserRole);

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByTransactional(Long[] userIds)
    {
        this.removeByIds(Arrays.asList(userIds));

        //删除中间表信息
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", userIds));

        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePermissions(Long userId, Long[] roleIds)
    {
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
        this.clearUserAuthorityInfo(this.getById(userId).getUsername());

        return Result.success(sysUserRoleList);
    }

    @Override
    public Result resetPassword(SysUser sysUser)
    {
        //设置默认加密密码和更新日期
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        this.updateById(sysUser);

        return Result.success("重置密码成功");
    }

    @Override
    public Result updatePassword(SysUser sysUser, PasswordDto passwordDto)
    {
        sysUser.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        this.updateById(sysUser);

        return Result.success("更新密码成功");
    }

}
