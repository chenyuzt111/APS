package com.benewake.system.excel.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsAttendance;
import com.benewake.system.entity.ApsTimeSheet;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.AttendanceTemplate;
import com.benewake.system.excel.transfer.AttendanceExcelToPo;
import com.benewake.system.service.ApsAttendanceService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AttendanceListener extends AnalysisEventListener<AttendanceTemplate> {


    private final List<ApsAttendance> attendances = new ArrayList<>();

    private final AttendanceExcelToPo attendanceExcelToPo;

    private final ApsTimeSheet apsTimeSheet;

    private final ApsAttendanceService apsAttendanceService;

    private final Integer type;

    public AttendanceListener(AttendanceExcelToPo attendanceExcelToPo, ApsTimeSheet apsTimeSheet, ApsAttendanceService apsAttendanceService, Integer type) {
        this.attendanceExcelToPo = attendanceExcelToPo;
        this.apsTimeSheet = apsTimeSheet;
        this.apsAttendanceService = apsAttendanceService;
        this.type = type;
    }

    @Override
    public void invoke(AttendanceTemplate data, AnalysisContext context) {
        //todo 格式校验(时间格式)
        ApsAttendance attendance = attendanceExcelToPo.convert(data, apsTimeSheet);
        attendances.add(attendance);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            apsAttendanceService.remove(null);
        }
        apsAttendanceService.saveBatch(attendances);
    }
}
