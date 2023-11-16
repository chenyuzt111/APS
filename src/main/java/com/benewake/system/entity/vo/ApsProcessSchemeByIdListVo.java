package com.benewake.system.entity.vo;

import com.benewake.system.entity.dto.ApsProcessSchemeDto;
import lombok.Data;

import java.util.List;

@Data
public class ApsProcessSchemeByIdListVo {
    List<ApsProcessSchemeVo> apsProcessSchemeVoList;

    private String productFamily;

    private Integer number;
}
