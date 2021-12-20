package com.ruoyan.security;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.lang.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Package: com.ruoyan.security
 * @ClassName: JwtAccessDeniedHandler
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 15:43
 * @Description: 权限不足拒绝访问处理器
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException
    {
        //设置response的类型格式和状态码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ServletOutputStream outputStream = response.getOutputStream();

        Result result = Result.fail(HttpServletResponse.SC_FORBIDDEN,e.getMessage());

        //将失败结果写入到输出流中
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
