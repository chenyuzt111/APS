package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOutRequest;
import com.benewake.system.entity.kingdee.KingdeeOutRequest;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class KingdeeToApsOutRequest {
    public ApsOutRequest convert(KingdeeOutRequest kingdeeOutRequest, Integer version) throws ParseException {
        if (kingdeeOutRequest == null || version == null) {
            return null;
        }
        ApsOutRequest apsOutRequest=new ApsOutRequest();
        apsOutRequest.setFMaterialCode(kingdeeOutRequest.getFMaterialId());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        Date parse = dateFormat.parse(kingdeeOutRequest.getF_ora_BackDate());
        apsOutRequest.setFReturnDate(kingdeeOutRequest.getF_ora_BackDate());
        apsOutRequest.setVersion(version);

        return apsOutRequest;
    }
}
