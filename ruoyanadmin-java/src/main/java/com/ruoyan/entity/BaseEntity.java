package com.ruoyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Package: com.ruoyan.entity
 * @ClassName: BaseEntity
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/23 8:21
 * @Description: 实体类基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseEntity implements Serializable
{
    private final static long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字段创建时间
     */
    private LocalDateTime created;

    /**
     * 字段更新时间
     */
    private LocalDateTime updated;

    /**
     * 状态
     */
    private Integer statu;
}
