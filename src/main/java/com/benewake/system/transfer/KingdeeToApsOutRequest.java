package com.benewake.system.transfer;

import com.benewake.system.entity.kingdee.KingdeeOutRequest;
import generator.domain.ApsOutRequest;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsOutRequest {
    public ApsOutRequest convert(KingdeeOutRequest kingdeeOutRequest,Integer version){
        if (kingdeeOutRequest == null || version == null) {
            return null;
        }
        ApsOutRequest apsOutRequest=new ApsOutRequest();
        apsOutRequest.setFMaterialCode(kingdeeOutRequest.getFMaterialId());
        apsOutRequest.setFReturnDate(kingdeeOutRequest.getF_ora_BackDate());
        apsOutRequest.setVersion(version);

        return apsOutRequest;
    }
}
