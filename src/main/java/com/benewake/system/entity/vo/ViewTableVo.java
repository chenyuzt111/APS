package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ViewTableVo {
    /**
     *
     */
    @TableId(value = "view_id", type = IdType.AUTO)
    private Integer viewId;

    /**
     *
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     *
     */
    @TableField(value = "view_name")
    private String viewName;

//    /**
//     *
//     */
//    @TableField(value = "table_id")
//    private Integer tableId;

}
