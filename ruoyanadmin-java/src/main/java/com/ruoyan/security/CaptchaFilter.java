package com.ruoyan.security;

import com.ruoyan.commom.exception.CaptchaException;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Package: com.ruoyan.security
 * @ClassName: CaptchaFilter
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 9:45
 * @Description: 验证码过滤器
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter
{
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    /**
     * 验证码过滤器逻辑
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String url = request.getRequestURI();

        if("/login".equals(url) && "POST".equals(request.getMethod()))
        {
            try
            {
                //比对验证码
                validate(request);
            }
            catch (CaptchaException e)
            {
                //将异常交给登录认证失败处理器
                loginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }

        filterChain.doFilter(request,response);
    }

    /**
     * 校验验证码逻辑
     *
     * @param request
     */
    private void validate(HttpServletRequest request)
    {
        String code = request.getParameter("code");
        String key = request.getParameter("token");

        if(StringUtils.isBlank(code))
        {
            throw new CaptchaException("输入的验证码为空");
        }

        if(StringUtils.isBlank(key))
        {
            throw new CaptchaException("验证码已过期");
        }

        if(!code.equals(redisUtil.hget(Const.CAPTCHA_KEY,key)))
        {
            throw new CaptchaException("验证码错误");
        }

        //验证完验证码后立刻删除验证码，一次性使用
        redisUtil.hdel(Const.CAPTCHA_KEY,key);
    }
}
