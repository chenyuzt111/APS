package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName aps_product_family_process_scheme_management
 */
@TableName(value ="aps_product_family_process_scheme_management")
@Data
public class ApsProductFamilyProcessSchemeManagement implements Serializable {
    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 当前工艺方案name
     */
    @TableField(value = "cur_process_scheme_name")
    private String curProcessSchemeName;

    /**
     * 最优工艺方案name
     */
    @TableField(value = "optimal_process_scheme_name")
    private String optimalProcessSchemeName;

    /**
     * 经济批量
     */
    @TableField(value = "order_number")
    private Integer orderNumber;

    /**
     * 产线平衡率
     */
    @TableField(value = "production_line_balance_rate")
    private BigDecimal productionLineBalanceRate;

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

    /**
     * 人数
     */
    @TableField(value = "number")
    private Integer number;

    /**
     * 产品族
     */
    @TableField(value = "product_family")
    private String productFamily;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}