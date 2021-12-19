package com.ruoyan.service;

import com.ruoyan.commom.lang.Result;

import java.io.IOException;

/**
 * @Package: com.ruoyan.service
 * @ClassName: CaptchaService
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/13 8:33
 * @Description: 验证码服务层
 */
public interface CaptchaService
{
    /**
     * 生成验证码键值存入redis中并返回验证码key值
     * @return Result
     * @throws IOException 抛出异常程序继续执行
     */
    Result getCaptcha() throws IOException;
}
