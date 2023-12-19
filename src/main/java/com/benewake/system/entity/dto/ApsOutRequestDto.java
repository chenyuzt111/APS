package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName aps_out_request
 */
@TableName(value ="aps_out_request")
@Data
public class ApsOutRequestDto implements Serializable {


    /**
     * 物料编码
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料编码
     */
    private String materialName;

    /**
     * 归还日期
     */

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date returnDate;

    /**
     * 版本号
     */
    private String chVersion;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
