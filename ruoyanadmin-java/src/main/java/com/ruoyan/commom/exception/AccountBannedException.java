package com.ruoyan.commom.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Package: com.ruoyan.commom.exception
 * @ClassName: BanException
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/10 8:25
 * @Description: 账户禁用异常
 */
public class AccountBannedException extends AuthenticationException
{
    public AccountBannedException(String msg)
    {
        super(msg);
    }
}
