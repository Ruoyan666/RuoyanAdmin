package com.ruoyan.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @Package: com.ruoyan.security
 * @ClassName: AccountUser
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 20:43
 * @Description: 自定义UserDetails实现类
 */
public class MyCustomizeUser implements UserDetails
{
    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 用户已获得权限列表
     */
    private final Collection<GrantedAuthority> authorities;

    /**
     * 账户过期标志
     */
    private final boolean accountNonExpired;

    /**
     * 账户锁定标志
     */
    private final boolean accountNonLocked;

    /**
     * 凭证过期标志
     */
    private final boolean credentialsNonExpired;

    /**
     * 账户启用标志
     */
    private final boolean enabled;

    public MyCustomizeUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities)
    {
        this(userId,username, password, true, true, true, true, authorities);
    }

    public MyCustomizeUser(Long userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities)
    {
        Assert.isTrue(username != null && !"".equals(username) && password != null, "Cannot pass null or empty values to constructor");
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = (Collection<GrantedAuthority>) authorities;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return null;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }
}
