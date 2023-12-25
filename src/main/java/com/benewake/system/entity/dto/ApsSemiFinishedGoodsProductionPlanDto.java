package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.python.antlr.ast.Str;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName aps_semi_finished_goods_production_plan
 */
@TableName(value ="aps_semi_finished_goods_production_plan")
@Data
public class ApsSemiFinishedGoodsProductionPlanDto implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_code")
    private String fmaterialCode;



    @ExcelProperty("物料名称")
    @TableField(value = "f_material_name")
    private String fmaterialName;

    /**
     * 
     */
    @ExcelProperty("数量")
    @TableField(value = "f_quantity")
    private String fquantity;

    /**
     * 
     */
    @ExcelProperty("开始制作时间")
    @TableField(value = "f_start_time")
    private Date fstartTime;

    /**
     * 
     */
    @ExcelProperty("需入库时间")
    @TableField(value = "f_required_delivery_time")
    private Date frequiredDeliveryTime;

    @ExcelProperty("版本号")
    private String chVersion;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}