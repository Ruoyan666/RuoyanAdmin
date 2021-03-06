package com.ruoyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyan.entity.SysUserRole;
import com.ruoyan.mapper.SysUserRoleMapper;
import com.ruoyan.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Package: com.ruoyan.service.impl
 * @ClassName: SysUserRoleServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:59
 * @Description: 用户角色服务层接口实现类
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper,SysUserRole> implements SysUserRoleService
{
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;


    @Override
    public List<SysUserRole> getUserRoleInfoByUserId(Long userId)
    {
        return sysUserRoleMapper.getUserRoleInfoByUserId(userId);
    }
}
