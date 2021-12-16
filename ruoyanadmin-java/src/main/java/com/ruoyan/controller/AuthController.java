package com.ruoyan.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;

/**
 * @Package: com.ruoyan.controller
 * @ClassName: KaptchaController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:33
 * @Description: 权限控制器
 */
@Api(tags = "验证码及用户信息控制层", value = "AuthController")
@RestController
public class AuthController extends BaseController
{
    @Autowired
    Producer producer;

    @Autowired
    CaptchaService captchaService;


    /**
     * 生成验证码键值存入redis中并返回验证码key值
     *
     * @return Result
     */
    @ApiOperation(value = "测试获取验证码值接口")
    @GetMapping("/captcha")
    public Result captcha() throws IOException
    {
        return captchaService.getCaptcha();
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param principal
     * @return Result
     */
    @ApiOperation(value = "根据用户名获取用户信息接口")
    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal)
    {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        SysUserDto sysUserDto = sysUserService.getUserInfo(sysUser);

        return Result.success(sysUserDto);
    }
}
