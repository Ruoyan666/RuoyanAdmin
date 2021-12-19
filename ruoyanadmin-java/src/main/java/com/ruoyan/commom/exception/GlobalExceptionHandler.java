package com.ruoyan.commom.exception;

import com.ruoyan.commom.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Package: com.ruoyan.commom.exception
 * @ClassName: GlobalExceptionHandler
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 9:44
 * @Description: 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{
    /**
     * 处理运行时异常
     *
     * @param e
     * @return result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handlerResult(RuntimeException e)
    {
        log.error("运行时异常:--------{}",e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = DuplicateKeyException.class)
    public Result handlerResult(DuplicateKeyException e)
    {
        log.error("数据库表唯一约束列异常:--------{}",e.getMessage());
        return Result.fail("请检查添加的属性字段是否与已有数据重复");
    }

    /**
     * 处理非法参数异常
     *
     * @param e
     * @return result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handlerResult(IllegalArgumentException e)
    {
        log.error("Assert异常：---------{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 处理实体校验异常
     *
     * @param e
     * @return result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handlerResult(MethodArgumentNotValidException e)
    {

        BindingResult result = e.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();

        log.error("实体类校验异常：---------{}", objectError.getDefaultMessage());
        return Result.fail(objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result handlerResult(AccessDeniedException e)
    {

        log.error("权限不足异常：---------{}", e.getMessage());
        return Result.fail("权限不足，不允许访问");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AccountBannedException.class)
    public Result handlerResult(AccountBannedException e)
    {

        log.error("账户禁用异常：---------{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = CheckSuperAdminException.class)
    public Result handlerResult(CheckSuperAdminException e)
    {

        log.error("超级管理员角色操作异常：---------{}", e.getMessage());
        return Result.fail(e.getMessage());
    }
}
