package com.benewake.system.transfer;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import org.springframework.stereotype.Component;

@Component
public class ApsProcessCapacityEntityToVo {
    public ApsProcessCapacityVo convert(ApsProcessCapacity object) {
        ApsProcessCapacityVo apsProcessCapacityVo = new ApsProcessCapacityVo();
        apsProcessCapacityVo.setId(object.getId());
        apsProcessCapacityVo.setBelongingProcess(object.getBelongingProcess());
        apsProcessCapacityVo.setProcessName(object.getProcessName());
        apsProcessCapacityVo.setProcessNumber(object.getProcessNumber());
        apsProcessCapacityVo.setProductFamily(object.getProductFamily());
        apsProcessCapacityVo.setPackagingMethod(object.getPackagingMethod());
        apsProcessCapacityVo.setStandardTime(object.getStandardTime().toString());
        apsProcessCapacityVo.setMaxPersonnel(object.getMaxPersonnel());
        apsProcessCapacityVo.setMinPersonnel(object.getMinPersonnel());

        return apsProcessCapacityVo;
    }
}
