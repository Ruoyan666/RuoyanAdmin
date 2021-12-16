package com.ruoyan.config;

import com.ruoyan.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Package: com.ruoyan.config
 * @ClassName: SecurityConfig
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 18:00
 * @Description: Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    CaptchaFilter captchaFilter;

    /**
     * jwt权限过滤器Bean
     * @return jwtAuthenticationFilter
     */
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception
    {
        return new JwtAuthenticationFilter(authenticationManager());
    }

    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 用户密码加密Bean
     * @return BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    /**
     * 可信任URL白名单
     */
    private final static String[] URL_WHILE_LIST =
            {
                    "/login",
                    "/logout",
                    "/captcha",
                    "/favicon.ico",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/v2/api-docs/**",
                    "/webjars/springfox-swagger-ui/**",
            };

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.cors()
        .and()
                .csrf().disable()
                //登录配置
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)

                //注销配置
        .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                //禁用session
        .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //配置拦截规则
        .and()
                .authorizeRequests()
                .antMatchers(URL_WHILE_LIST).permitAll()
                .anyRequest().authenticated()


                //配置自定义过滤器
        .and()
        .addFilter(jwtAuthenticationFilter())
        .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)

        //异常处理器

        .exceptionHandling()
        //配置jwt权限处理器
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler)
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailsService);
    }
}
