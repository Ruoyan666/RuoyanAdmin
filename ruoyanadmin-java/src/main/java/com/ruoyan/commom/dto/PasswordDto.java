package com.ruoyan.commom.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Package: com.ruoyan.commom.dto
 * @ClassName: PasswordDto
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/3 9:29
 * @Description: 密码Dto类
 */
@Data
public class PasswordDto implements Serializable
{
    private final static long serialVersionUID = 1L;

    @NotBlank(message = "旧密码不能为空")
    private String currentPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
