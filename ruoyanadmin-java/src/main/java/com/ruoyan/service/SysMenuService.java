package com.ruoyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.commom.dto.SysMenuDto;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;

import java.util.List;

/**
 * @Package: com.ruoyan.service
 * @ClassName: SysMenuService
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:56
 * @Description: 菜单服务层接口
 */
public interface SysMenuService extends IService<SysMenu>
{
    /**
     * 获取当前用户的菜单导航栏信息
     *
     * @return List<SysMenuDto>
     */
    List<SysMenuDto> getCurrentUserNav();

    /**
     * 获取所有菜单项的树结构列表
     *
     * @return List<SysMenu>
     */
    List<SysMenu> treeList();

    /**
     * 将菜单项列表转换成菜单项树结构列表
     *
     * @param sysMenuList
     * @return List<SysMenu>
     */
    List<SysMenu> buildMenuTree(List<SysMenu> sysMenuList);

    /**
     * 利用事务对菜单项进行保存操作
     *
     * @param sysMenu
     * @return Result
     */
    Result saveByTransactional(SysMenu sysMenu);

    /**
     * 利用事务对菜单项进行更新操作
     * 第一个参数为需更新的菜单项实体类
     * 第二个参数为所有菜单项的树结构列表
     *
     * @param sysMenu
     * @param sysMenuList
     * @return Result
     */
    Result updateByTransactional(SysMenu sysMenu, List<SysMenu> sysMenuList);

    /**
     * 利用事务对菜单项进行删除操作（可批量删除）
     *
     * @param menuId
     * @return Result
     */
    Result deleteByTransactional(Long menuId);

}
