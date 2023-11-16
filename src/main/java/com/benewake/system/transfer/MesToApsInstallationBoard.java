package com.benewake.system.transfer;

import com.benewake.system.entity.ApsInstallationBoard;
import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.mes.MesInstallationBoard;
import com.benewake.system.entity.mes.MesPcbaBurn;
import org.springframework.stereotype.Component;

@Component
public class MesToApsInstallationBoard {
    public ApsInstallationBoard convert(MesInstallationBoard mesInstallationBoard , Integer version) {
        if (mesInstallationBoard == null || version == null) {
            return null;
        }
        ApsInstallationBoard apsInstallationBoard = new ApsInstallationBoard();
        apsInstallationBoard.setProductionOrderNumber(mesInstallationBoard.getProductionOrderNumber());
        apsInstallationBoard.setMaterialCode(mesInstallationBoard.getMaterialCode());
        apsInstallationBoard.setMaterialName(mesInstallationBoard.getMaterialName());
        apsInstallationBoard.setBurnInCompletionQuantity(mesInstallationBoard.getBurnInCompletionQuantity());
        apsInstallationBoard.setBurnQualifiedCount(mesInstallationBoard.getBurnQualifiedCount());
        apsInstallationBoard.setTotalNumber(mesInstallationBoard.getTotalNumber());
        apsInstallationBoard.setUnburnQualifiedCount(mesInstallationBoard.getUnBurnQualifiedCount());
        apsInstallationBoard.setVersion(version);

        return apsInstallationBoard;

    }
}
