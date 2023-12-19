package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@TableName(value = "aps_receive_notice")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsReceiveNoticeDto implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ColumnWidth(20) // 单据编号
    @ExcelProperty("单据编号")
    @JsonProperty("billNo")
    private String billNo;

    @ColumnWidth(20) // 物料编码
    @ExcelProperty("物料编码")
    @JsonProperty("materialId")
    private String materialId;

    @ColumnWidth(30) // 物料名称
    @ExcelProperty("物料名称")
    @JsonProperty("materialName")
    private String materialName;

    @ColumnWidth(15) // 实收数量
    @ExcelProperty("实收数量")
    @JsonProperty("mustQty")
    private Integer mustQty;

    @ColumnWidth(15) // 检测数量
    @ExcelProperty("检测数量")
    @JsonProperty("checkQty")
    private Integer checkQty;

    @ColumnWidth(15) // 合格数量
    @ExcelProperty("合格数量")
    @JsonProperty("receiveQty")
    private Integer receiveQty;

    @ColumnWidth(20) // 让步接收数量(基本数量)
    @ExcelProperty("让步接收数量(基本数量)")
    @JsonProperty("csnReceiveBaseQty")
    private Integer csnReceiveBaseQty;

    @ColumnWidth(15) // 入库数量
    @ExcelProperty("入库数量")
    @JsonProperty("inStockQty")
    private Integer inStockQty;

    @ColumnWidth(15) // 版本号
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
