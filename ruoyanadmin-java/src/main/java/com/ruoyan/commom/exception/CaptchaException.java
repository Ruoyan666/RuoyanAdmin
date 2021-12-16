package com.ruoyan.commom.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Package: com.ruoyan.commom.exception
 * @ClassName: CaptchaException
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 9:53
 * @Description: 验证码错误异常
 */
public class CaptchaException extends AuthenticationException
{
    public CaptchaException(String msg)
    {
        super(msg);
    }
}
