package com.benewake.system.excel.transfer;

import com.benewake.system.entity.vo.ApsFimRequestVo;
import com.benewake.system.excel.entity.ExcelFimRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FimRequestVoTiExcelList {
    public List<ExcelFimRequest> convert(List<ApsFimRequestVo> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }

        ArrayList<ExcelFimRequest> excelFimRequests = new ArrayList<>();
        for (ApsFimRequestVo apsFimRequestVo : object) {
            ExcelFimRequest excelFimRequest = new ExcelFimRequest();
            excelFimRequest.setFDocumentNumber(apsFimRequestVo.getFDocumentNumber());
            excelFimRequest.setFCreator(apsFimRequestVo.getFCreator());
            excelFimRequest.setFMaterialCode(apsFimRequestVo.getFMaterialCode());
            excelFimRequest.setFMaterialName(apsFimRequestVo.getFMaterialName());
            excelFimRequest.setFCustomerName(apsFimRequestVo.getFCustomerName());
            excelFimRequest.setFSalesperson(apsFimRequestVo.getFSalesperson());
            excelFimRequest.setFQuantity(apsFimRequestVo.getFQuantity());
            Date fExpectedDeliveryDate = apsFimRequestVo.getFExpectedDeliveryDate();
            if (fExpectedDeliveryDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(fExpectedDeliveryDate);
                excelFimRequest.setFExpectedDeliveryDate(formattedDate);
            }
            excelFimRequest.setFDocumentType(apsFimRequestVo.getFDocumentType());
            excelFimRequests.add(excelFimRequest);
        }
        return excelFimRequests;
    }
}
