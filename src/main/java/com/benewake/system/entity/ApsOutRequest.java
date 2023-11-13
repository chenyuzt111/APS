package com.benewake.system.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName aps_out_request
 */
@TableName(value ="aps_out_request")
@Data
public class ApsOutRequest implements Serializable {
    /**
     * 物料编码
     */
    @TableField(value = "f_material_code")
    private String fMaterialCode;

    /**
     * 归还日期
     */
    @TableField(value = "f_return_date")
    private Date fReturnDate;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
