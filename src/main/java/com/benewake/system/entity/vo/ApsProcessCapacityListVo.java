package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工序与产能表
 *
 * @TableName aps_process_capacity
 */

@Data
public class ApsProcessCapacityListVo extends PageParam implements Serializable  {

    private List<ApsProcessCapacityVo> apsProcessCapacityVo;

}