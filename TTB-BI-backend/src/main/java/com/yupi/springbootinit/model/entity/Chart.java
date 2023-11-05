package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图表信息表
 * @author 下水道的小老鼠
 * @TableName chart
 */
@TableName(value ="chart")
@Data
public class Chart implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 图标名称
     */
    private String name;

    /**
     * 分析目标
     */
    @TableField(value = "goal")
    private String goal;

    /**
     * 图表数据
     */
    @TableField(value = "chartData")
    private String chartData;

    /**
     * 图表类型
     */
    @TableField(value = "chartType")
    private String chartType;

    /**
     * 任务状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 执行信息
     */
    @TableField(value = "execMessage")
    private String execMessage;

    /**
     * 生成的图表数据
     */
    @TableField(value = "genChart")
    private String genChart;

    /**
     * 生成的分析结论
     */
    @TableField(value = "genResult")
    private String genResult;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}