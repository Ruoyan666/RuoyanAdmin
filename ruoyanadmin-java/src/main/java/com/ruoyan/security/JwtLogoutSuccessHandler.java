package com.ruoyan.security;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Package: com.ruoyan.security
 * @ClassName: JwtLogoutSuccessHandler
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/29 15:45
 * @Description: 注销成功Jwt数据清空处理器
 */
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler
{
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        if(authentication != null)
        {
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //将jwt数据清空
        response.setHeader(jwtUtil.getHeader(),"");

        Result result = Result.success("注销成功");

        //将成功结果写入到输出流中
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
