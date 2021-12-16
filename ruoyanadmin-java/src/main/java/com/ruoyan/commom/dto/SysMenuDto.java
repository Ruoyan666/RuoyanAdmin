package com.ruoyan.commom.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.ruoyan.commom.dto
 * @ClassName: SysMenuDto
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/29 16:59
 * @Description:
 */
@Data
public class SysMenuDto implements Serializable
{
    private final static long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String title;

    private String icon;

    private String path;

    private String component;

    private List<SysMenuDto> children = new ArrayList<>();
}
