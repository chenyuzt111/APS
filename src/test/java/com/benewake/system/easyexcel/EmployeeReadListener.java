package com.benewake.system.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeReadListener extends AnalysisEventListener<ApsDailyDataUploadExcelTest> {

    //员工集合
    private static List<ApsDailyDataUploadExcelTest> employeeList = new ArrayList<>();

    // 每读一样，会调用该invoke方法一次
    @Override
    public void invoke(ApsDailyDataUploadExcelTest data, AnalysisContext context) {
        employeeList.add(data);
        System.out.println("解析到一条数据：" + data);
    }

    // 全部读完之后，会调用该方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("全部解析完成");
    }

    /**
     * 返回读取到的员工集合
     * @return
     */
    public static List<ApsDailyDataUploadExcelTest> getStudentList() {
        return employeeList;
    }
}