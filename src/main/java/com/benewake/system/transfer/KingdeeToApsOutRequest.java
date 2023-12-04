package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOutRequest;
import com.benewake.system.entity.kingdee.KingdeeOutRequest;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsOutRequest {
    public ApsOutRequest convert(KingdeeOutRequest kingdeeOutRequest, Integer version) throws ParseException {
        if (kingdeeOutRequest == null || version == null) {
            return null;
        }

        ApsOutRequest apsOutRequest=new ApsOutRequest();
        apsOutRequest.setFMaterialCode(kingdeeOutRequest.getFMaterialId());
        Date parse = BenewakeStringUtils.parse(kingdeeOutRequest.getF_ora_BackDate(), "yyyy-MM-dd'T'HH:mm:ss");
        apsOutRequest.setFReturnDate(parse);
        apsOutRequest.setVersion(version);

        return apsOutRequest;
    }
}
