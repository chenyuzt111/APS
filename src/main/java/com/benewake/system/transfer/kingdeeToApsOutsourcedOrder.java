package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOutsourcedOrder;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedOrder;
import org.springframework.stereotype.Component;

@Component
public class kingdeeToApsOutsourcedOrder {

    public ApsOutsourcedOrder convert(KingdeeOutsourcedOrder kingdeeOutsourcedOrder , Integer version) {
        if (kingdeeOutsourcedOrder == null || version == null) {
            return null;
        }
        ApsOutsourcedOrder apsOutsourcedOrder = new ApsOutsourcedOrder();
        apsOutsourcedOrder.setFBillNo(kingdeeOutsourcedOrder.getFBillNo());
        apsOutsourcedOrder.setFBillType(kingdeeOutsourcedOrder.getFBillType());
        apsOutsourcedOrder.setFMaterialId(kingdeeOutsourcedOrder.getFMaterialId());
        apsOutsourcedOrder.setFMaterialName(kingdeeOutsourcedOrder.getFMaterialName());
        apsOutsourcedOrder.setFQty(kingdeeOutsourcedOrder.getFQty());
        apsOutsourcedOrder.setFStatus(kingdeeOutsourcedOrder.getFStatus());
        apsOutsourcedOrder.setFPickMtrlStatus(kingdeeOutsourcedOrder.getFPickMtrlStatus());
        apsOutsourcedOrder.setFStockInQty(kingdeeOutsourcedOrder.getFStockInQty());
        apsOutsourcedOrder.setFBomId(kingdeeOutsourcedOrder.getFBomId());
        apsOutsourcedOrder.setFDzMaterialName(kingdeeOutsourcedOrder.getF_ora_FDZMaterialID2());
        apsOutsourcedOrder.setVersion(version);

        return apsOutsourcedOrder;
    }

}
