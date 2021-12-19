package com.ruoyan.commom.lang;

/**
 * @Package: com.ruoyan.commom.lang
 * @ClassName: Const
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:50
 * @Description: 常量通用类
 */
public class Const
{
    /**
     * 验证码Key常量
     */
    public final static String CAPTCHA_KEY = "captcha";

    /**
     * 数据库字段状态码默认开启
     */
    public final static Integer STATUS_ON = 1;

    /**
     * 数据库字段状态码默认禁用
     */
    public final static Integer STATUS_OFF = 0;

    /**
     * 初始化默认用户密码
     */
    public final static String DEFAULT_PASSWORD = "maxwell";

    /**
     * 初始化默认用户头像
     */
    public final static String DEFAULT_AVATAR = "https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg";

    /**
     * 初始化普通用户身份码
     */
    public final static String DEFAULT_ROLENAME = "普通用户";

    /**
     * 超级管理员身份状态码
     */
    public final static String ADMIN_CODE = "admin";

    /**
     * 更新方法通用名
     */
    public final static String UPDATE = "update";

    /**
     * 删除方法通用名
     */
    public final static String DELETE = "delete";

    /**
     * 操纵角色下菜单项权限方法通用名
     */
    public final static String PERM = "perm";

    /**
     * 操作用户下角色项权限方法通用名
     */
    public final static String ROLEPERM = "rolePerm";
}
