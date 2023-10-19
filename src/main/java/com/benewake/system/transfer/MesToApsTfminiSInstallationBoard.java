package com.benewake.system.transfer;

import com.benewake.system.entity.ApsInstallationBoard;
import com.benewake.system.entity.ApsTfminiSInstallationBoard;
import com.benewake.system.entity.mes.MesInstallationBoard;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSInstallationBoard {
    public ApsTfminiSInstallationBoard convert(MesInstallationBoard mesInstallationBoard , Integer version) {
        if (mesInstallationBoard == null || version == null) {
            return null;
        }
        ApsTfminiSInstallationBoard apsTfminiSInstallationBoard = new ApsTfminiSInstallationBoard();
        apsTfminiSInstallationBoard.setProductionOrderNumber(mesInstallationBoard.getProductionOrderNumber());
        apsTfminiSInstallationBoard.setMaterialCode(mesInstallationBoard.getMaterialCode());
        apsTfminiSInstallationBoard.setMaterialName(mesInstallationBoard.getMaterialName());
        apsTfminiSInstallationBoard.setBurnInCompletionQuantity(mesInstallationBoard.getBurnInCompletionQuantity());
        apsTfminiSInstallationBoard.setBurnQualifiedCount(mesInstallationBoard.getBurnQualifiedCount());
        apsTfminiSInstallationBoard.setVersion(version);

        return apsTfminiSInstallationBoard;
    }
}
