package com.benewake.system.transfer;

import com.benewake.system.entity.ApsSnLabeling;
import com.benewake.system.entity.mes.MesSnLabeling;
import org.springframework.stereotype.Component;

@Component
public class MesToApsSnLabeling {
    public ApsSnLabeling convert(MesSnLabeling mesSnLabeling, Integer version) {
        if (mesSnLabeling == null || version == null) {
            return null;
        }

        ApsSnLabeling apsSnLabeling = new ApsSnLabeling();
        apsSnLabeling.setProductionOrderNumber(mesSnLabeling.getProductionOrderNumber());
        apsSnLabeling.setMaterialCode(mesSnLabeling.getMaterialCode());
        apsSnLabeling.setMaterialName(mesSnLabeling.getMaterialName());
        apsSnLabeling.setBurnInCompletionQuantity(mesSnLabeling.getBurnInCompletionQuantity());
        apsSnLabeling.setBurnQualifiedCount(mesSnLabeling.getBurnQualifiedCount());
        apsSnLabeling.setTotalNumber(mesSnLabeling.getTotalNumber());
        apsSnLabeling.setUnburnQualifiedCount(mesSnLabeling.getUnBurnQualifiedCount());
        apsSnLabeling.setVersion(version);

        return apsSnLabeling;
    }
}
