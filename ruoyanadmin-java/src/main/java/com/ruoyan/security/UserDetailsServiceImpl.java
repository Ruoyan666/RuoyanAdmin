package com.ruoyan.security;

import com.ruoyan.commom.exception.AccountBannedException;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Package: com.ruoyan.security
 * @ClassName: UserDetailServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 20:36
 * @Description: UserDetailService实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    SysUserService sysUserService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser sysUser = sysUserService.getByUsername(username);

        if(sysUser.getStatu() == 0)
        {
            throw new AccountBannedException("账户已被禁用");
        }
        else if(sysUser == null)
        {
            throw new UsernameNotFoundException("用户名或密码不正确");
        }


        return new MyCustomizeUser(sysUser.getId(),sysUser.getUsername(),sysUser.getPassword(),
                getUserAuthorities(sysUser.getId()));

    }


    /**
     * 获取用户权限信息（角色，菜单权限）
     * @param userId
     * @return List<GrantedAuthority>
     */
    public List<GrantedAuthority> getUserAuthorities(Long userId)
    {
        //获取角色，菜单操作权限
        String authority = sysUserService.getUserAuthorityInfo(userId);

        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
