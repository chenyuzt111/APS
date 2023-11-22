package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @ColumnWidth(34)
    @ExcelProperty("物料名称")
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @ColumnWidth(20) 
    @ExcelProperty("到期日")
    @JsonProperty("expiryDate")
    @TableField(value = "f_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date fExpiryDate;

    @ColumnWidth(15) 
    @ExcelProperty("锁库数量")
    @JsonProperty("lockQty")
    @TableField(value = "f_lock_qty")
    private Integer fLockQty;

    @ColumnWidth(20) 
    @ExcelProperty("批号")
    @JsonProperty("lot")
    @TableField(value = "f_lot")
    private String fLot;

    @ColumnWidth(15) 
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
