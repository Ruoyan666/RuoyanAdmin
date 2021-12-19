package com.ruoyan.security;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.exception.AccountBannedException;
import com.ruoyan.commom.exception.CaptchaException;
import com.ruoyan.commom.lang.Result;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Package: com.ruoyan.security
 * @ClassName: LoginFailureHandler
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 7:44
 * @Description: 登录认证失败处理器
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        Result result;

        if(e.getClass() == CaptchaException.class)
        {
            result = Result.fail(e.getMessage());
        }
        else if(e.getCause().getClass() == AccountBannedException.class)
        {
            result = Result.fail(e.getMessage());
        }
        else
        {
            result = Result.fail("用户名或密码错误!");
        }

        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
