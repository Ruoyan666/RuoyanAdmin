package com.ruoyan.utils;

import com.ruoyan.commom.lang.Const;
import com.ruoyan.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ruoyan.commom.lang.Const.ADMIN_CODE;

/**
 * @Package: com.ruoyan.utils
 * @ClassName: RuoyanUtil
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/19 9:26
 * @Description: 自定义工具类
 */
@Component
public class RuoyanUtil
{
    @Autowired
    SysRoleService sysRoleService;

    public void checkSuperAdmin(String code, String className, String functionName)
    {
        if(Const.ADMIN_CODE.equals(code))
        {
            sysRoleService.superAdminCheck(className, functionName);
        }
    }
}
