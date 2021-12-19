package com.ruoyan.commom.exception;


/**
 * @Package: com.ruoyan.commom.exception
 * @ClassName: checkSuperAdminException
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/19 9:12
 * @Description: 超级管理员身份检查异常
 */
public class CheckSuperAdminException extends RuntimeException
{
    public CheckSuperAdminException(String message)
    {
        super(message);
    }
}
