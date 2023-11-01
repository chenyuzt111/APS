package com.benewake.system.entity.vo;

import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ProcessSchemeManagementVoPage extends PageParam {

    private List<ProcessSchemeManagementVo> processSchemeManagementVos;

}
