package com.ruoyan.security;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Package: com.ruoyan.security
 * @ClassName: LoginSuccessHandler
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 7:50
 * @Description: 登录认证成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler
{
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //生成jwt，将jwt放到请求头中
        String jwt = jwtUtil.generateToken(authentication.getName());
        response.setHeader(jwtUtil.getHeader(),jwt);


        Result result = Result.success("?????");

        //将成功结果写入到输出流中
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
