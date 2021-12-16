package com.ruoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyan.entity.SysRole;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Package: com.ruoyan.mapper
 * @ClassName: SysRoleMapper
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:54
 * @Description: 角色表mapper接口
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole>
{
    /**
     * 通过用户Id查询用户的角色信息，并返回一个角色信息列表
     *
     * @param userId
     * @return List<SysRole>
     */
    public List<SysRole> listRolesByUserId(Long userId);
}
