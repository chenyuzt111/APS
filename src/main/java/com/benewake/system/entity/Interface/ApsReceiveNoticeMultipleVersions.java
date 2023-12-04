package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsReceiveNotice;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_receive_notice
 */
@TableName(value ="aps_receive_notice")
@Data
public class ApsReceiveNoticeMultipleVersions extends ApsReceiveNotice implements Serializable {
    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}