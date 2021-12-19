package com.ruoyan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ruoyan.entity
 * @ClassName: sysUser
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:25
 * @Description: 用户表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 城市地址
     */
    private String city;

    /**
     * 最后一次登录时间
     */
    private LocalDateTime lastLogin;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户拥有的角色列表
     */
    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();
}
