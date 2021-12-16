package com.ruoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.entity.SysMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Package: com.ruoyan.mapper
 * @ClassName: SysMenuMapper
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:54
 * @Description: 菜单表mapper接口
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu>
{
}
