package com.ruoyan.security;

import cn.hutool.core.util.StrUtil;
import com.ruoyan.commom.exception.AccountBannedException;
import com.ruoyan.commom.exception.CaptchaException;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.SysUserService;
import com.ruoyan.utils.JwtUtil;
import com.ruoyan.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Package: com.ruoyan.security
 * @ClassName: JwtAuthenticationFilter
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/30 8:32
 * @Description: Jwt权限过滤器
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter
{
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {

        String jwt = request.getHeader(jwtUtil.getHeader());
        if (StrUtil.isBlankOrUndefined(jwt))
        {
            chain.doFilter(request, response);
            return;
        }

        Claims claim = jwtUtil.getClaimByToken(jwt);

        if (claim == null)
        {
            throw new JwtException("token 异常");
        }

        if (jwtUtil.isTokenExpired(claim))
        {
            throw new JwtException("token已过期");
        }

        String username = claim.getSubject();

        // 获取用户信息
        SysUser sysUser = sysUserService.getByUsername(username);

        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(username, null, userDetailsService.getUserAuthorities(sysUser.getId()));

        SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(request, response);
    }

}
