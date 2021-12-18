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
        return sysMenuService.update(sysMenu);
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
    @Transactional(rollbackFor = Exception.class)
    public Result update(@ApiParam(value = "被更新实体类信息") @Validated @RequestBody SysMenu sysMenu)
    {
        //所有菜单项的树结构列表
        List<SysMenu> sysMenuList = sysMenuService.treeList();

        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);


        //级联更新菜单状态，因为是三级结构，因此需要遍历比较查询出更新的菜单项是否为顶级菜单
        //若为顶级菜单，则直接更新顶级菜单和顶级菜单下的所有子菜单项状态即可
        //若不为顶级菜单，则先找出属于该菜单下的所有子菜单项，最后更新该菜单和菜单下所有子菜单项状态
        if(sysMenu.getParentId() == 0L)
        {
            updateMenuChildrenStatus(sysMenuList, sysMenu);
        }
        else
        {
            //遍历整个树结构，找到当前菜单项结点
            for (SysMenu menu : sysMenuList)
            {
                for (SysMenu child : menu.getChildren())
                {
                    //若当前菜单项存在子菜单项，则更新子菜单项状态
                    if(child.getId().equals(sysMenu.getId()))
                    {
                        updateMenuChildrenStatus(menu.getChildren(), sysMenu);
                    }
                }
            }

        }


        //清除redis中与该菜单所有相关的权限缓存
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());

        return Result.success(sysMenu);
    }


    /**
     * 该方法为更新某菜单项下所有子菜单项状态信息
     * 第一个参数为子菜单项列表
     * 第二个参数为子菜单项列表的父菜单项
     *
     * @param sysMenuList
     * @param sysMenu
     */
    public void updateMenuChildrenStatus(List<SysMenu> sysMenuList, SysMenu sysMenu)
    {
        for (SysMenu menu : sysMenuList)
        {
            if(menu.getId().equals(sysMenu.getId()) && menu.getChildren().size() > 0)
            {
                for (SysMenu child : menu.getChildren())
                {
                    child.setUpdated(LocalDateTime.now());
                    child.setStatu(sysMenu.getStatu());
                    sysMenuService.updateById(child);

                    updateMenuChildrenStatus(menu.getChildren(), child);
                }
            }
        }

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
    @Transactional
    public Result delete(@ApiParam(value = "菜单Id") @PathVariable(name = "id") Long menuId)
    {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", menuId));
        if(count > 0)
        {
            return Result.fail("当前菜单项下还存在有子菜单项，无法删除");
        }

        //清除redis中与该菜单所有相关的权限缓存
        sysUserService.clearUserAuthorityInfoByMenuId(menuId);

        sysMenuService.removeById(menuId);
        //同步删除中间关联表信息
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", menuId));

        return Result.success("操作成功");
    }

}
