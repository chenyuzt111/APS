package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExcelSchemeManagement implements Serializable {


    @ColumnWidth(14)
    @ExcelProperty("产品族")
    private String productFamily;

    @ColumnWidth(24)
    @ExcelProperty("当前工艺方案")
    private String currentProcessScheme;

    @ColumnWidth(24)
    @ExcelProperty("最优工艺方案")
    private String optimalProcessPlan;

    /**
     * 经济批量
     */
    @ColumnWidth(12)
    @ExcelProperty("经济批量")
    private Integer orderNumber;

    @ColumnWidth(7)
    @ExcelProperty("人数")
    private Integer number;

    /**
     * 产线平衡率
     */
    @ColumnWidth(16)
    @ExcelProperty("产线平衡率")
    private String productionLineBalanceRate;

    /**
     * 完成时间
     */
    @ColumnWidth(13)
    @ExcelProperty("完成时间")
    private BigDecimal completionTime;

    /**
     * 可以释放人数
     */
    @ColumnWidth(17)
    @ExcelProperty("可以释放人数")
    private Integer releasableStaffCount;

    /**
     * 总释放时间
     */
    @ColumnWidth(15)
    @ExcelProperty("总释放时间")
    private String totalReleaseTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
