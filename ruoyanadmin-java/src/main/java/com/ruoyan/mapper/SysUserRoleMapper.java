package com.ruoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyan.entity.SysUserRole;
import org.springframework.stereotype.Repository;

/**
 * @Package: com.ruoyan.mapper
 * @ClassName: SysUserRoleMapper
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:55
 * @Description: 用户角色关联表mapper接口
 */
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole>
{
}
