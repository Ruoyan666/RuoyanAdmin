package com.ruoyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Package: com.ruoyan.entity
 * @ClassName: SysRoleMenu
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:53
 * @Description: 角色菜单关联表实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysRoleMenu
{
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单Id
     */
    private Long menuId;

    /**
     * 角色Id
     */
    private Long roleId;
}
