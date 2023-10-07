package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用于跟踪表的版本历史记录的表格
 * @TableName aps_table_version
 */
@TableName(value ="aps_table_version")
@Data
public class ApsTableVersion implements Serializable {
    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}