package com.benewake.system.service;

import com.benewake.system.entity.ApsAttendance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsAttendanceParam;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import com.benewake.system.entity.vo.PageResultVo;

/**
* @author ASUS
* @description 针对表【aps_attendance】的数据库操作Service
* @createDate 2023-12-05 15:56:08
*/
public interface ApsAttendanceService extends IService<ApsAttendance> {

    PageResultVo<ApsAttendanceVo> getAttendanceManList(Integer page, Integer size);

    boolean addOrUpdateAttendance(ApsAttendanceParam attendanceParam);
}
