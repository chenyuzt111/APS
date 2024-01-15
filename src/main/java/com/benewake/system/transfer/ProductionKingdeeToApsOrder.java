package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.utils.BenewakeStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class ProductionKingdeeToApsOrder {

    public ApsOrder convert(KingdeeProductionOrder kingdeeProductionOrder) throws ParseException {
        if (kingdeeProductionOrder == null) {
            return null;
        }
        ApsOrder apsOrder = new ApsOrder();
        apsOrder.setBillNo(kingdeeProductionOrder.getFBillNo());
        apsOrder.setBillType(kingdeeProductionOrder.getFBillType());
        apsOrder.setMaterialId(kingdeeProductionOrder.getFMaterialId());
        apsOrder.setQty(kingdeeProductionOrder.getFQty());
        apsOrder.setStatus(kingdeeProductionOrder.getFStatus());
        apsOrder.setPickMtrlStatus(kingdeeProductionOrder.getFPickMtrlStatus());
        apsOrder.setStockInQuaAuxQty(kingdeeProductionOrder.getFStockInQuaAuxQty());
        apsOrder.setBomId(kingdeeProductionOrder.getFBomId());
        apsOrder.setDzmaterialId(kingdeeProductionOrder.getFMaterialId());
        String fPlanFinishDate = kingdeeProductionOrder.getFPlanFinishDate();

        Date parse = BenewakeStringUtils.parse(fPlanFinishDate, "yyyy-MM-dd'T'HH:mm:ss");
        apsOrder.setPlannedCompletionTime(parse);

        apsOrder.setFormName("生产订单列表");
        return apsOrder;
    }

}
