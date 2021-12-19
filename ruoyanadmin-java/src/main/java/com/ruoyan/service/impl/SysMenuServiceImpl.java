package com.ruoyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.dto.SysMenuDto;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.entity.SysRoleMenu;
import com.ruoyan.entity.SysUser;
import com.ruoyan.mapper.SysMenuMapper;
import com.ruoyan.mapper.SysUserMapper;
import com.ruoyan.service.SysMenuService;
import com.ruoyan.service.SysRoleMenuService;
import com.ruoyan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ruoyan.service.impl
 * @ClassName: SysMenuServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:58
 * @Description: 菜单服务层接口实现类
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper,SysMenu> implements SysMenuService
{

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenuDto> getCurrentUserNav()
    {
        //拿到用户名并获取用户对象
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = sysUserService.getByUsername(username);

        //根据用户Id获取导航栏中菜单Id值列表
        List<Long> navMenuIds = sysUserMapper.getNavMenuIds(sysUser.getId());

        //根据获取到的菜单Id值列表拿到菜单信息列表
        List<SysMenu> sysMenuList = this.listByIds(navMenuIds);

        //将菜单列表转换成树状结构
        List<SysMenu> menuTree = buildMenuTree(sysMenuList);

        //将树状结构转换成DTO
        return convert(menuTree);
    }

    @Override
    public List<SysMenu> treeList()
    {
        //先获取所有的菜单信息
        List<SysMenu> sysMenuList = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));

        //转换成树状结构
        return buildMenuTree(sysMenuList);
    }

    /**
     * 将菜单项树结构转换成菜单项Dto结构
     *
     * @param menuTree
     * @return List<SysMenuDto>
     */
    private List<SysMenuDto> convert(List<SysMenu> menuTree)
    {
        List<SysMenuDto> menuDtoList = new ArrayList<>();

        //对树状结构进行遍历，将树状结构转换成DTO
        for (SysMenu sysMenu : menuTree)
        {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(sysMenu.getId());
            dto.setName(sysMenu.getPerms());
            dto.setTitle(sysMenu.getName());
            dto.setComponent(sysMenu.getComponent());
            dto.setPath(sysMenu.getPath());

            //当前结点若有子结点，则将子结点也转换为DTO
            if(sysMenu.getChildren().size() > 0)
            {
                dto.setChildren(convert(sysMenu.getChildren()));
            }

            menuDtoList.add(dto);
        }

        return menuDtoList;
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> sysMenuList)
    {
        List<SysMenu> menuTree = new ArrayList<>();

        // 先找寻子结点
        for (SysMenu sysMenu : sysMenuList)
        {
            for (SysMenu menu : sysMenuList)
            {
                //当内循环当前结点的父结点Id等于外层循环结点Id，说明此时为子结点
                if(menu.getParentId().equals(sysMenu.getId()))
                {
                    sysMenu.getChildren().add(menu);
                }
            }

            //当前结点无父结点，说明为顶级结点
            if(sysMenu.getParentId() == 0L)
            {
                menuTree.add(sysMenu);
            }
        }

        return menuTree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveByTransactional(SysMenu sysMenu)
    {
        sysMenu.setCreated(LocalDateTime.now());
        this.save(sysMenu);

        return Result.success(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateByTransactional(SysMenu sysMenu, List<SysMenu> sysMenuList)
    {
        if(sysMenu == null)
        {
            return Result.fail("未发现该菜单项，可能已被删除");
        }

        sysMenu.setUpdated(LocalDateTime.now());
        this.updateById(sysMenu);

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

        //清除redis中关于菜单项的所有数据缓存
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());

        return Result.success(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByTransactional(Long menuId)
    {
        if(this.getById(menuId) == null)
        {
            return Result.fail("未发现该菜单项,可能已被其它管理员删除");
        }

        int count = this.count(new QueryWrapper<SysMenu>().eq("parent_id", menuId));
        if(count > 0)
        {
            return Result.fail("当前菜单项下还存在有子菜单项，无法删除");
        }

        //清除redis中与该菜单所有相关的权限缓存
        sysUserService.clearUserAuthorityInfoByMenuId(menuId);

        //同步删除中间关联表信息
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", menuId));

        this.removeById(menuId);

        return Result.success("OK");
    }


    /**
     * 该方法为更新某菜单项下所有子菜单项状态信息
     * 第一个参数为子菜单项列表
     * 第二个参数为子菜单项列表的父菜单项
     *
     * @param sysMenuList
     * @param sysMenu
     */
    private void updateMenuChildrenStatus(List<SysMenu> sysMenuList, SysMenu sysMenu)
    {
        for (SysMenu menu : sysMenuList)
        {
            if(menu.getId().equals(sysMenu.getId()) && menu.getChildren().size() > 0)
            {
                for (SysMenu child : menu.getChildren())
                {
                    child.setUpdated(LocalDateTime.now());
                    child.setStatu(sysMenu.getStatu());
                    this.updateById(child);

                    updateMenuChildrenStatus(menu.getChildren(), child);
                }
            }
        }

    }
}
