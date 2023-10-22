package com.benewake.system.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApsProcessSchemeParams {

    List<ApsProcessSchemeParam> apsProcessSchemeParam;

    /**
     * 人数
     */

    private Integer number;

}
