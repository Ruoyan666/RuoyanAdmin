package com.ruoyan.controller;

import cn.hutool.json.JSONUtil;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.SysUserService;
import com.ruoyan.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

/**
 * @Package: com.ruoyan.controller
 * @ClassName: TestController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 9:34
 * @Description: 测试控制器
 */
@Api(tags = "测试接口控制层", value = "测试Controller")
@RestController
public class TestController
{
    @Autowired
    SysUserService sysUserService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    /**
     * 测试获取用户信息接口
     *
     * @return Result
     */
    @ApiOperation(value = "测试通过权限获取用户信息接口")
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public Result test()
    {
        return Result.success(sysUserService.list());
    }

    /**
     * 生成测试密码加密数据方法
     *
     * @return Result
     */
    @ApiOperation(value = "测试通过权限获取随机密码")
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/test/password")
    public Result password()
    {
        //将测试密码数据进行加密后的密文密码
        String password = bCryptPasswordEncoder.encode("111111");

        //匹配密码是否正确
        boolean matches = bCryptPasswordEncoder.matches("111111", password);

        System.out.println("匹配结果为:" + matches);

        return Result.success(password);
    }

}
