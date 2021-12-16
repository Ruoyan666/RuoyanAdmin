package com.ruoyan.security;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Package: com.ruoyan.security
 * @ClassName: JwtAuthenticationEntryPoint
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 15:42
 * @Description: jwt权限认证处理器入口
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException
    {
        //设置response的类型格式和状态码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ServletOutputStream outputStream = response.getOutputStream();

        Result result = Result.fail(HttpServletResponse.SC_UNAUTHORIZED,"认证失败，请登录");

        //将失败结果写入到输出流中
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
