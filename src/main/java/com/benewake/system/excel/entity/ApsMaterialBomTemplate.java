//package com.benewake.system.excel.entity;
//
//import com.alibaba.excel.annotation.ExcelProperty;
//import com.alibaba.excel.annotation.write.style.ColumnWidth;
//import com.baomidou.mybatisplus.annotation.TableField;
//
//import java.io.Serializable;
//
//import lombok.Data;
//
//@Data
//public class ApsMaterialBomTemplate implements Serializable {
//
//
//    @ExcelProperty("父项物料编码")
//    @ColumnWidth(20) // 设置列宽为20
//    private String fMaterialId;
//
//
//    @ExcelProperty("数据状态")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fDocumentStatus;
//
//    @ExcelProperty("子项物料编码")
//    @ColumnWidth(20) // 设置列宽为20
//    private String fMaterialIdChild;
//
//
//    @ExcelProperty("用量：分子")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fNumerator;
//
//    @ExcelProperty("用量：分母")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fDenominator;
//
//    @ExcelProperty("变动损耗率%")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fFixScrapQtyLot;
//
//    @ExcelProperty("子项类型")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fScrapRate;
//
//    @ExcelProperty("替代方案")
//    @ColumnWidth(15) // 设置列宽为15
//    private String fMaterialType;
//
//    @ExcelProperty("项次")
//    @ColumnWidth(15) // 设置列宽
//    private String fReplaceType;
//
//    @ExcelProperty("父项物料编码")
//    @ColumnWidth(20) // 设置列宽为20
//    private String fReplaceGroupBop;
//
//    @ExcelProperty("工序")
//    @ColumnWidth(15) // 设置列宽为15
//    private String process;
//
//    @ExcelProperty("BOM版本")
//    @ColumnWidth(15) // 设置列宽为15
//    private String bomVersion;
//
//
//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
//}
