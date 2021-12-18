package com.ruoyan.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyan.commom.dto.SysMenuDto;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.entity.SysRoleMenu;
import com.ruoyan.entity.SysUser;
import com.ruoyan.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @Package: com.ruoyan.controller
 * @ClassName: SysMenuController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:52
 * @Description: 菜单管理控制器
 */
@Api(tags = "菜单管理控制层", value = "菜单Controller")
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController
{

    /**
     * 获取当前用户的菜单权限信息
     *
     * @param principal
     * @return Result
     */
    @ApiOperation(value = "获取当前用户菜单权限信息接口")
    @GetMapping("/nav")
    public Result nav(@ApiParam(value = "当前用户名") Principal principal)
    {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        //获取权限信息
        String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());

        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");


        //获取导航栏信息
        List<SysMenuDto> navLists = sysMenuService.getCurrentUserNav();


        return Result.success(MapUtil.builder()
                .put("authoritys", authorityInfoArray)
                .put("nav",navLists)
                .map()
        );
    }

    /**
     * 根据指定菜单Id获取菜单项信息
     *
     * @param menuId
     * @return Result
     */
    @ApiOperation(value = "根据指定菜单Id和获取菜单项信息接口")
    @GetMapping("/info/{id}")
    public Result info(@ApiParam(value = "菜单Id") @PathVariable(name = "id") Long menuId)
    {
        return Result.success(sysMenuService.getById(menuId));
    }

    /**
     * 获取所有菜单项信息
     *
     * @return Result
     */
    @ApiOperation(value = "获取所有菜单项信息接口")
    @PreAuthorize("hasAuthority('sys:menu:manage')")
    @GetMapping("/list")
    public Result list()
    {
        List<SysMenu> sysMenuList = sysMenuService.treeList();

        return Result.success(sysMenuList);
    }

    /**
     * 新增菜单项信息
     *
     * @param sysMenu
     * @return Result
     */
    @ApiOperation(value = "新增菜单项信息接口")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    @PostMapping("/save")
    public Result save(@ApiParam(value = "需新增实体类信息") @Validated @RequestBody SysMenu sysMenu)
    {
        return sysMenuService.saveByTransactional(sysMenu);
    }

    /**
     * 更新菜单项信息
     *
     * @param sysMenu
     * @return Result
     */
    @ApiOperation(value = "更新菜单项信息接口")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    @PostMapping("/update")
    public Result update(@ApiParam(value = "被更新实体类信息") @Validated @RequestBody SysMenu sysMenu)
    {
        //所有菜单项的树结构列表
        List<SysMenu> sysMenuList = sysMenuService.treeList();

        Result result = sysMenuService.updateByTransactional(sysMenu, sysMenuList);

        return result;
    }



    /**
     * 根据指定菜单Id删除菜单项
     *
     * @param menuId
     * @return Result
     */
    @ApiOperation(value = "根据指定菜单Id删除菜单项接口")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @PostMapping("/delete/{id}")
    public Result delete(@ApiParam(value = "菜单Id") @PathVariable(name = "id") Long menuId)
    {
        Result result = sysMenuService.deteleByTransactional(menuId);

        return result;
    }

}
