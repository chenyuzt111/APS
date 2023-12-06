package com.benewake.system.service;

import com.benewake.system.entity.ApsHolidayTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ASUS
* @description 针对表【aps_holiday_table】的数据库操作Service
* @createDate 2023-12-06 10:31:16
*/
public interface ApsHolidayTableService extends IService<ApsHolidayTable> {

    void updateHoliday();
}
