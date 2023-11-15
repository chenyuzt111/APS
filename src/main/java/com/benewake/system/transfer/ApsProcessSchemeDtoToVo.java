package com.benewake.system.transfer;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.dto.ApsProcessSchemeDto;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import com.benewake.system.entity.vo.ApsProcessSchemeVo;
import org.springframework.stereotype.Component;

@Component
public class ApsProcessSchemeDtoToVo {
    public ApsProcessSchemeVo convert(ApsProcessSchemeDto object) {
        if (object == null) {
            return null;
        }
        ApsProcessSchemeVo apsProcessSchemeVo = new ApsProcessSchemeVo();
        apsProcessSchemeVo.setId(object.getId());
        apsProcessSchemeVo.setCurrentProcessScheme(object.getCurrentProcessScheme());
        apsProcessSchemeVo.setBelongingProcess(object.getBelongingProcess());
        apsProcessSchemeVo.setProcessId(object.getProcessId());
        apsProcessSchemeVo.setProcessName(object.getProcessName());
        apsProcessSchemeVo.setProcessNumber(object.getProcessNumber());
        apsProcessSchemeVo.setProductFamily(object.getProductFamily());
        apsProcessSchemeVo.setPackagingMethod(object.getPackagingMethod());
        if (object.getStandardTime() != null) {
            apsProcessSchemeVo.setStandardTime(object.getStandardTime());
        }
        apsProcessSchemeVo.setMaxPersonnel(object.getMaxPersonnel());
        apsProcessSchemeVo.setMinPersonnel(object.getMinPersonnel());
        apsProcessSchemeVo.setEmployeeName(object.getEmployeeName());
        apsProcessSchemeVo.setNumber(object.getNumber());
        if (object.getState() == null) {
            return apsProcessSchemeVo;
        }
        if (object.getState()) {
            apsProcessSchemeVo.setState("有效");
        } else {
            apsProcessSchemeVo.setState("无效");
        }

        return apsProcessSchemeVo;
    }

}
