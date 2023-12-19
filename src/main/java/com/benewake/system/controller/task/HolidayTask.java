package com.benewake.system.controller.task;

import com.benewake.system.service.ApsHolidayTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HolidayTask {


    @Autowired
    private ApsHolidayTableService apsHolidayTableService;

    @Scheduled(cron = "0 0 0 1 * ? ")
    public void cleanTasks() {
        apsHolidayTableService.updateHoliday();
    }
}
