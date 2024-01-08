package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName(value ="aps_common_functions")
@Data
public class ApsCommonFunctionsDto {
    /**
     *
     */
    @TableId
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 标题
     */
    private String label;

    /**
     * 界面名
     */
    private String name;

    /**
     * 前端路径
     */
    private String path;
}
