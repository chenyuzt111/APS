package com.benewake.system.transfer;

import com.benewake.system.entity.ApsSnLabeling;
import com.benewake.system.entity.ApsTfminiSSnLabeling;
import com.benewake.system.entity.mes.MesSnLabeling;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSSnLabeling {
    public ApsTfminiSSnLabeling convert(MesSnLabeling mesSnLabeling, Integer version) {
        if (mesSnLabeling == null || version == null) {
            return null;
        }

        ApsTfminiSSnLabeling apsTfminiSSnLabeling = new ApsTfminiSSnLabeling();
        apsTfminiSSnLabeling.setProductionOrderNumber(mesSnLabeling.getProductionOrderNumber());
        apsTfminiSSnLabeling.setMaterialCode(mesSnLabeling.getMaterialCode());
//        apsTfminiSSnLabeling.setMaterialName(mesSnLabeling.getMaterialName());
        apsTfminiSSnLabeling.setBurnInCompletionQuantity(mesSnLabeling.getBurnInCompletionQuantity());
        apsTfminiSSnLabeling.setBurnQualifiedCount(mesSnLabeling.getBurnQualifiedCount());
        apsTfminiSSnLabeling.setUnburnQualifiedCount(mesSnLabeling.getUnBurnQualifiedCount());
        apsTfminiSSnLabeling.setTotalNumber(mesSnLabeling.getTotalNumber());
        apsTfminiSSnLabeling.setVersion(version);

        return apsTfminiSSnLabeling;
    }
}
