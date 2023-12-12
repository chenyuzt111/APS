package com.benewake.system.transfer;

import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsProductionOrder {

    public ApsProductionOrder convert(KingdeeProductionOrder kingdeeProductionOrder , Integer version) throws ParseException {
        if (kingdeeProductionOrder == null || version == null) {
            return null;
        }
        ApsProductionOrder apsProductionOrder = new ApsProductionOrder();
        apsProductionOrder.setFBillNo(kingdeeProductionOrder.getFBillNo());
        apsProductionOrder.setFBillType(kingdeeProductionOrder.getFBillType());
//        apsProductionOrder.setFBillTypeId(kingdeeProductionOrder.getFBILLTYPEID());
        apsProductionOrder.setFMaterialId(kingdeeProductionOrder.getFMaterialId());
        apsProductionOrder.setFQty(kingdeeProductionOrder.getFQty());
        apsProductionOrder.setFStatus(kingdeeProductionOrder.getFStatus());
        apsProductionOrder.setFPickMtrlStatus(kingdeeProductionOrder.getFPickMtrlStatus());
        apsProductionOrder.setFStockInQuaAuxQty(kingdeeProductionOrder.getFStockInQuaAuxQty());
        apsProductionOrder.setFBomId(kingdeeProductionOrder.getFBomId());
        apsProductionOrder.setFMaterialName(kingdeeProductionOrder.getFMaterialName());
        Date parse = BenewakeStringUtils.parse(kingdeeProductionOrder.getFPlanFinishDate(), "yyyy-MM-dd'T'HH:mm:ss");
        apsProductionOrder.setPlannedCompletionTime(parse);
        apsProductionOrder.setFDzMaterialName(kingdeeProductionOrder.getF_ora_FDZMaterialID2());
        apsProductionOrder.setVersion(version);

        return apsProductionOrder;
    }

}
