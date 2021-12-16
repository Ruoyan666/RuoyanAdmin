package com.ruoyan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyan.service.*;
import com.ruoyan.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Package: com.ruoyan.controller
 * @ClassName: BaseController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:35
 * @Description: 基础控制层
 */
@Api(tags = "基础控制层", value = "基础Controller")
public class BaseController
{
    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysUserService sysUserService;

    /**
     * 获取分页数据
     *
     * @return Page
     */
    @ApiOperation(value = "获取分页数据接口")
    public Page getPage()
    {
        int currentPage = ServletRequestUtils.getIntParameter(request,"current", 1);
        int size = ServletRequestUtils.getIntParameter(request,"size", 10);

        return new Page(currentPage, size);
    }
}
