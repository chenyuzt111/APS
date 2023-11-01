package com.benewake.system.entity.vo;

import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ApsProcessSchemeVoPage extends PageParam {

    private List<ApsProcessSchemeVo> apsProcessSchemeVo;

}
