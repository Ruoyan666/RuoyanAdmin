package com.ruoyan.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.ruoyan.commom.dto.SysUserDto;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.CaptchaService;
import com.ruoyan.service.SysUserService;
import com.ruoyan.utils.UpAvatarNameUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    SysUserService sysUserService;


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

    @PostMapping("/sys/avatarUpdate/{userId}")
    public String avatarUpdate(@RequestParam("file") MultipartFile file,
                               @PathVariable(value = "userId") Long userId,
                               @RequestBody String username)
    {
        SysUser sysUser = sysUserService.getById(userId);
        Assert.notNull(sysUser, "未找到该用户");

        try
        {
            byte[] bytes = file.getBytes();
            String imageFileName = file.getOriginalFilename();
            String fileName = UpAvatarNameUtil.getPhotoName("img",imageFileName);
            Path path = Paths.get("E:\\IdeaWorkspace\\ruoyanAdmin\\ruoyanadmin-vue\\src\\assets\\avatar\\images\\" + fileName);
            //为本地目录
            //写入文件
            Files.write(path, bytes);

            sysUser.setAvatar(fileName);
            sysUserService.updateById(sysUser);


            System.out.println(fileName+"\n");
            return fileName;//返回文件名字
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
