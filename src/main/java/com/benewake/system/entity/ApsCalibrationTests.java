package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_calibration_tests
 */
@TableName(value ="aps_calibration_tests")
@Data
public class ApsCalibrationTests implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 生产订单编号
     */
    @TableField(value = "productionOrderNumber")
    private String productionordernumber;

    /**
     * 物料编码
     */
    @TableField(value = "materialCode")
    private String materialcode;

    /**
     * 物料名称
     */
    @TableField(value = "materialName")
    private String materialname;

    /**
     * 本次校准测试完成数
     */
    @TableField(value = "burnInCompletionQuantity")
    private String burnincompletionquantity;

    /**
     * 校准合格数
     */
    @TableField(value = "BurnQualifiedCount")
    private String burnqualifiedcount;

    /**
     * 测试工装编号
     */
    @TableField(value = "BurnFixtureNumber")
    private String burnfixturenumber;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}