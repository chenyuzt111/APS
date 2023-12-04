package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用于跟踪表的版本历史记录的表格
 * @TableName aps_table_version
 */
@TableName(value ="aps_table_version")
@Data
@Builder
public class ApsTableVersion implements Serializable {
    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "table_id")
    private Integer tableId;

    /**
     * 
     */
    @TableField(value = "table_version")
    private Integer tableVersion;

    /**
     * 版本号
     */
    @TableField(value = "version_number")
    private Integer versionNumber;

    /**
     * 版本时间
     */
    @TableField(value = "version_time")
    private Date versionTime;

    /**
     * 状态 1-成功 2-编辑中 3-失效
     */
    @TableField(value = "state")
    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}