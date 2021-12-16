package com.ruoyan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ruoyan.entity
 * @ClassName: SysRole
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:30
 * @Description: 角色表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysRole extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "角色名不能为空")
    private String name;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String code;

    /**
     * 备注
     */
    private String remark;

    /**
     * 角色下子菜单ID
     */
    @TableField(exist = false)
    private List<Long> menuIds = new ArrayList<>();
}
