package com.benewake.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateInterfaceDataVo {

    @ApiModelProperty(value = "类型(1:)")
    List<Integer> ids;

}
