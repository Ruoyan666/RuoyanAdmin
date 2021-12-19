package com.ruoyan.commom.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Package: com.ruoyan.commom.dto
 * @ClassName: SysUserDto
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/30 9:41
 * @Description: 用户项实体类Dto
 */
@Data
public class SysUserDto implements Serializable
{
    private final static long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String avatar;

    private LocalDateTime created;
}
