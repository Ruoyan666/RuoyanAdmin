package com.ruoyan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysRole;
import com.ruoyan.entity.SysRoleMenu;
import com.ruoyan.utils.RuoyanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Package: com.ruoyan.controller
 * @ClassName: SysRoleController
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:52
 * @Description: 角色管理控制器
 */
@Api(tags = "角色管理控制层", value = "角色Controller")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController
{
    /**
     * 当前类名
     */
    private final String className = this.getClass().getName();

    /**
     * 方法名
     */
    private String functionName;

    @Autowired
    RuoyanUtil ruoyanUtil;


    /**
     * 根据指定角色Id获取角色信息
     *
     * @param roleId
     * @return Result
     */
    @ApiOperation(value = "根据指定角色Id获取角色信息接口")
    @GetMapping("/info/{id}")
    public Result info(@ApiParam(value = "角色Id") @PathVariable(name = "id") Long roleId)
    {
        SysRole sysRole = sysRoleService.getById(roleId);

        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(
                new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));

        //获取角色相关联的菜单Id列表
        List<Long> menuIdsList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        sysRole.setMenuIds(menuIdsList);

        return Result.success(sysRole);
    }

    /**
     * 根据角色名模糊查询角色信息接口
     *
     * @param roleName
     * @return Result
     */
    @ApiOperation(value = "根据角色名模糊查询角色信息接口")
    @PreAuthorize("hasAuthority('sys:role:manage')")
    @GetMapping("/list")
    @ApiImplicitParam(value = "角色名(可为空)", name = "roleName", dataTypeClass = String.class)
    public Result list(String roleName)
    {
        Page<SysRole> sysRolePageData = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>().like(StringUtils.isNotBlank(roleName), "name", roleName));

        return Result.success(sysRolePageData);
    }

    /**
     * 新增角色项信息
     *
     * @param sysRole
     * @return Result
     */
    @ApiOperation(value = "新增角色项信息接口")
    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@ApiParam(value = "需新增实体类信息") @Validated @RequestBody SysRole sysRole)
    {
        //设置创建日期和状态码
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(Const.STATUS_ON);

        sysRoleService.save(sysRole);

        return Result.success(sysRole);
    }

    /**
     * 更新角色项信息
     * 超级管理员角色不可被修改
     *
     * @param sysRole
     * @return Result
     */
    @ApiOperation(value = "更新角色项信息接口")
    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@ApiParam(value = "需更新实体类信息") @Validated @RequestBody SysRole sysRole)
    {
        SysRole authRole = sysRoleService.getById(sysRole);

        this.functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        ruoyanUtil.checkSuperAdmin(authRole.getCode(), this.className, this.functionName);

        return sysRoleService.updateByTransactional(sysRole);
    }

    /**
     * 根据指定的一组角色Id（有可能只有一个角色Id，有可能有多个角色Id）
     * 若只有一个角色Id，则只删除一个角色项
     * 若有多个角色Id，则为批量删除，可删除多个角色项
     * 超级管理员角色不可被删除
     *
     * @param roleIds
     * @return Result
     */
    @ApiOperation(value = "删除角色项接口（可批量删除）")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    public Result delete(@ApiParam(value = "角色Id数组") @RequestBody Long[] roleIds)
    {
        this.functionName = Thread.currentThread().getStackTrace()[1].getMethodName();

        for (Long roleId : roleIds)
        {
            SysRole authRole = sysRoleService.getById(roleId);

            ruoyanUtil.checkSuperAdmin(authRole.getCode(), this.className, this.functionName);
        }

        return sysRoleService.deleteByTransactional(roleIds);
    }

    /**
     * 根据角色Id和角色关联的菜单项Id修改角色下权限
     * 超级管理员角色不可被修改权限
     *
     * @param roleId
     * @param menuIds
     * @return Result
     */
    @ApiOperation(value = "修改角色权限信息接口")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @PostMapping("/perm/{roleId}")
    public Result perm(@ApiParam(value = "角色Id") @PathVariable(name = "roleId") Long roleId, @ApiParam(value = "与当前角色所关联的菜单Id数组") @RequestBody Long[] menuIds)
    {
        //针对超级管理员角色本身设置无法修改权限操作
        SysRole authRole = sysRoleService.getById(roleId);

        this.functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        ruoyanUtil.checkSuperAdmin(authRole.getCode(), this.className, this.functionName);

        return sysRoleService.updatePermissions(roleId, menuIds);
    }
}
