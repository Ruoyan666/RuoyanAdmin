package com.ruoyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.commom.dto.SysMenuDto;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.entity.SysMenu;
import com.ruoyan.entity.SysUser;
import com.ruoyan.mapper.SysMenuMapper;
import com.ruoyan.mapper.SysUserMapper;
import com.ruoyan.service.SysMenuService;
import com.ruoyan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    SysMenuMapper sysMenuMapper;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;

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
                if(menu.getParentId() == sysMenu.getId())
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
    public Result update(SysMenu sysMenu)
    {
        sysMenuMapper.updateById(sysMenu);

        return Result.success(sysMenu);
    }


}
