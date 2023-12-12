package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_product_family_machine_table
 */
@TableName(value ="aps_product_family_machine_table")
@Data
public class ApsProductFamilyMachineTable implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机器id
     */
    @TableField(value = "f_machine_id")
    @JsonProperty("fMachineId")
    private String fMachineId;

    /**
     * 机器名称
     */
    @TableField(value = "f_machine_name")
    @JsonProperty("fMachineName")
    private String fMachineName;

    /**
     * 产品族
     */
    @TableField(value = "f_product_family")
    @JsonProperty("fProductFamily")
    private String fProductFamily;


     /**
     * 适用工序
     */
    @JsonProperty("fProcessId")
    @TableField(value = "f_process_id")
    private String fProcessId;


    /**
     * 机器规格
     */
    @TableField(value = "f_machine_configuration")
    @JsonProperty("fMachineConfiguration")
    private String fMachineConfiguration;

    /**
     * 使用车间
     */
    @TableField(value = "f_workshop")
    @JsonProperty("fWorkshop")
    private String fWorkshop;

    /**
     * 是否可用
     */
    @TableField(value = "available")
    private String available;

    /**
     * 不可用日期
     */
    @TableField(value = "unavailable_dates")
    private String unavailableDates;


    /**
     * 
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}