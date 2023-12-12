package com.benewake.system.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApsProcessSchemeByIdListVo {
    List<ApsProcessSchemeVo> apsProcessSchemeVoList;

    private String productFamily;

    private Integer number;
}
