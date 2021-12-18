package com.ruoyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.commom.dto.SysMenuDto;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.mapper.SysMenuMapper;

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
    List<SysMenuDto> getCurrentUserNav();

    List<SysMenu> treeList();

    List<SysMenu> buildMenuTree(List<SysMenu> sysMenuList);

    /**
     * 对菜单项进行更新保存（利用事务)
     *
     * @param sysMenu
     * @return Result
     */
    Result saveByTransactional(SysMenu sysMenu);

    Result updateByTransactional(SysMenu sysMenu, List<SysMenu> sysMenuList);

    Result deteleByTransactional(Long menuId);

    void updateMenuChildrenStatus(List<SysMenu> sysMenuList, SysMenu sysMenu);

}
