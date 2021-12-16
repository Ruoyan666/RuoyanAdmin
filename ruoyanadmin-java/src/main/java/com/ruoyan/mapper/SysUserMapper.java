package com.ruoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyan.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Package: com.ruoyan.mapper
 * @ClassName: SysUserMapper
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 9:27
 * @Description: 用户表mapper接口
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser>
{
    /**
     * 通过指定用户Id查询用户拥有的菜单操作编码，并返回一个存放菜单编码的列表
     *
     * @param userId
     * @return List<Long>
     */
    List<Long> getNavMenuIds(Long userId);

    List<SysUser> listByMenuId(Long menuId);

}
