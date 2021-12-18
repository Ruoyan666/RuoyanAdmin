package com.ruoyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyan.entity.SysUserRole;

import java.util.List;

/**
 * @Package: com.ruoyan.service
 * @ClassName: SysUserRoleService
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:57
 * @Description: 用户角色服务层接口
 */
public interface SysUserRoleService extends IService<SysUserRole>
{
    /**
     * 根据用户Id查出用户关联的角色Id信息
     *
     * @param userId
     * @return List<SySUserRole>
     */
    List<SysUserRole> getUserRoleInfoByUserId(Long userId);
}
