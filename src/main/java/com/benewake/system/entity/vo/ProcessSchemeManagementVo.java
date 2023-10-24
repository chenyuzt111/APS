package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessSchemeManagementVo {
    private Integer id;
    /**
     * 产品族
     */
    @TableField(value = "product_family")
    private String productFamily;

    private String currentProcessScheme;

    private String optimalProcessScheme;

    /**
     * 经济批量
     */
    @TableField(value = "order_number")
    private Integer orderNumber;

    /**
     * 产线平衡率
     */
    @TableField(value = "production_line_balance_rate")
    private String productionLineBalanceRate;

    /**
     * 完成时间
     */
    @TableField(value = "completion_time")
    private BigDecimal completionTime;

    /**
     * 可以释放人数
     */
    @TableField(value = "releasable_staff_count")
    private Integer releasableStaffCount;

    /**
     * 总释放时间
     */
    @TableField(value = "total_release_time")
    private Double totalReleaseTime;
}
