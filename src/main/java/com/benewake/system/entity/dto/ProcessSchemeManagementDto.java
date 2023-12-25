package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessSchemeManagementDto {

    @ExcelIgnore
    private Integer id;

    /**
     * 产品族
     */
    @ExcelProperty("产品族")
    private String productFamily;

    @ExcelIgnore
    private Integer curId;

    @ExcelProperty("当前工艺方案")
    private String currentProcessScheme;

    @ExcelIgnore
    private Integer optimalId;

    @ExcelProperty("最优工艺方案")
    private String optimalProcessPlan;

    /**
     * 经济批量
     */
    @ExcelProperty("经济批量")
    private Integer orderNumber;

    /**
     * 产线平衡率
     */
   @ExcelProperty("产线平衡率")
    private BigDecimal productionLineBalanceRate;

    /**
     * 完成时间
     */
    @ExcelProperty("完成时间")
    private BigDecimal completionTime;

    /**
     * 可以释放人数
     */
    @ExcelProperty("可以释放人数")
    private Integer releasableStaffCount;

    /**
     * 总释放时间
     */
    @ExcelProperty("总释放时间")
    private Double totalReleaseTime;

    @ExcelProperty("人数")
    private Integer number;
}
