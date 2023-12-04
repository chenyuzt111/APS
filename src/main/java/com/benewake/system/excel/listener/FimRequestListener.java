package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsFimRequest;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelFimRequestTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsFimRequestService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FimRequestListener extends AnalysisEventListener<ExcelFimRequestTemplate> {

    private final List<ApsFimRequest> apsFimRequests = new ArrayList<>();

    private final ApsFimRequestService fimRequestService;

    private final Integer type;

    public FimRequestListener(ApsFimRequestService fimRequestService, Integer type) {
        this.fimRequestService = fimRequestService;
        this.type = type;
    }

    @Override
    public void invoke(ExcelFimRequestTemplate data, AnalysisContext context) {
        ApsFimRequest apsFimRequest = buildFimRequestPo(data);
        apsFimRequests.add(apsFimRequest);
    }

    private ApsFimRequest buildFimRequestPo(ExcelFimRequestTemplate data) {
        ApsFimRequest fimRequest = new ApsFimRequest();
        fimRequest.setFDocumentNumber(data.getFDocumentNumber());
        fimRequest.setFCreator(data.getFCreator());
        fimRequest.setFMaterialCode(data.getFMaterialCode());
        fimRequest.setFCustomerName(data.getFCustomerName());
        fimRequest.setFSalesperson(data.getFSalesperson());
        fimRequest.setFQuantity(data.getFQuantity());
        String dateString = data.getFExpectedDeliveryDate(); // 假设这是从 Excel 中读取的日期字符串
        if (dateString != null && !dateString.isEmpty()) {
            String[] dateFormats = {"yyyy-M-d", "yyyy/M/d"};  // 你可能的日期格式
            LocalDate parsedDate = null;
            for (String format : dateFormats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    parsedDate = LocalDate.parse(dateString, formatter);
                    break;
                } catch (Exception e) {
                    //尝试使用下一个格式解析
                }
            }

            if (parsedDate != null) {
                fimRequest.setFExpectedDeliveryDate(java.sql.Date.valueOf(parsedDate));
            } else {
                // 未能成功解析日期，根据需要处理错误情况
                throw new BeneWakeException("时间格式不正确 应为yyyy-MM-dd或yyyy/MM/dd的格式！");
            }
        }
        fimRequest.setFDocumentType(data.getFDocumentType());
        return fimRequest;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            fimRequestService.remove(null);
        }
        fimRequestService.saveBatch(apsFimRequests);
    }
}