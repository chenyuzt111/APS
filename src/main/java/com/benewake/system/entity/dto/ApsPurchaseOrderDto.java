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

@TableName(value ="aps_purchase_order")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsPurchaseOrderDto implements Serializable {

    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ColumnWidth(20) // 单据编号
    @ExcelProperty("单据编号")
    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @ColumnWidth(20) // 物料编号
    @ExcelProperty("物料编号")
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @ColumnWidth(30) // 物料名称
    @ExcelProperty("物料名称")
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @ColumnWidth(17) // 剩余收料数量
    @ExcelProperty("剩余收料数量")
    @JsonProperty("remainReceiveQty")
    @TableField(value = "f_remain_receive_qty")
    private Integer fRemainReceiveQty;

    @ColumnWidth(20) // 交货日期
    @ExcelProperty("交货日期")
    @JsonProperty("deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    @TableField(value = "f_delivery_date")
    private Date fDeliveryDate;

    @ColumnWidth(15) // 版本号
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
