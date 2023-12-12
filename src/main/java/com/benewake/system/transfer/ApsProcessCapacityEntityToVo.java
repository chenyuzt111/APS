package com.benewake.system.transfer;

import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import org.springframework.stereotype.Component;

@Component
public class ApsProcessCapacityEntityToVo {
    public ApsProcessCapacityVo convert(ApsProcessCapacityDto object) {
        ApsProcessCapacityVo apsProcessCapacityVo = new ApsProcessCapacityVo();
        apsProcessCapacityVo.setId(object.getId());
        apsProcessCapacityVo.setBelongingProcess(object.getBelongingProcess());
        apsProcessCapacityVo.setProcessId(object.getProcessId());
        apsProcessCapacityVo.setProcessName(object.getProcessName());
        apsProcessCapacityVo.setProcessNumber(object.getProcessNumber());
        apsProcessCapacityVo.setProductFamily(object.getProductFamily());
        apsProcessCapacityVo.setSwitchTime(object.getSwitchTime());
        apsProcessCapacityVo.setPackagingMethod(object.getPackagingMethod());
        if (object.getStandardTime() != null) {
            apsProcessCapacityVo.setStandardTime(object.getStandardTime().toString());
        }
        apsProcessCapacityVo.setMaxPersonnel(object.getMaxPersonnel());
        apsProcessCapacityVo.setMinPersonnel(object.getMinPersonnel());
        return apsProcessCapacityVo;
    }
}
