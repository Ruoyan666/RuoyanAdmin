package com.ruoyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Package: com.ruoyan.entity
 * @ClassName: SysUserRole
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:49
 * @Description: 用户角色关联表实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysUserRole
{
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 角色Id
     */
    private Long roleId;
}
