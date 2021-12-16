package com.ruoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.entity.SysRole;
import com.ruoyan.entity.SysUser;
import com.ruoyan.mapper.SysUserMapper;
import com.ruoyan.service.SysMenuService;
import com.ruoyan.service.SysRoleService;
import com.ruoyan.service.SysUserService;
import com.ruoyan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
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
    RedisUtil redisUtil;


    @Override
    public SysUser getByUsername(String username)
    {
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }


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
}
