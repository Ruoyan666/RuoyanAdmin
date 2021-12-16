package com.ruoyan.commom.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package: com.ruoyan.commom.lang
 * @ClassName: Result
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:04
 * @Description: 返回前端数据结果统一封装类
 */
@Data
public class Result implements Serializable
{

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 结果数据
     */
    private Object data;

    /**
     * 返回操作成功结果，结果包含状态码、提示信息、返回数据
     *
     * @param code
     * @param msg
     * @param data
     * @return result
     */
    public static Result success(Integer code, String msg, Object data)
    {
        Result result = new Result();

        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);

        return result;
    }

    /**
     * @param data
     * @return result
     */
    public static Result success(Object data)
    {
        return success(200,"操作成功", data);
    }


    /**
     * 返回操作失败结果，结果包含状态码、提示信息、返回数据
     *
     * @param code
     * @param msg
     * @param data
     * @return result
     */
    public static Result fail(Integer code, String msg, Object data)
    {
        Result result = new Result();

        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);

        return result;
    }

    /**
     * @param code
     * @param msg
     * @return result
     */
    public static Result fail(Integer code,String msg)
    {
        return fail(code, msg, null);
    }


    /**
     * @param msg
     * @return result
     */
    public static Result fail(String msg)
    {
        return fail(400, msg, null);
    }
}
