package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="aps_inventory_lock")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsInventoryLockDto implements Serializable {

    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ColumnWidth(20) 
    @ExcelProperty("物料编码")
    private String materialId;

    @ColumnWidth(34)
    @ExcelProperty("物料名称")
    private String materialName;

    @ColumnWidth(20) 
    @ExcelProperty("到期日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date expiryDate;

    @ColumnWidth(15) 
    @ExcelProperty("锁库数量")
    private Integer lockQty;

    @ColumnWidth(20) 
    @ExcelProperty("批号")
    private String lot;

    @ColumnWidth(15) 
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
