package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsAttendance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ASUS
* @description 针对表【aps_attendance】的数据库操作Mapper
* @createDate 2023-12-05 15:56:08
* @Entity com.benewake.system.entity.ApsAttendance
*/
@Mapper
public interface ApsAttendanceMapper extends BaseMapper<ApsAttendance> {

    Page<ApsAttendanceVo> getAttendancePage(Page<ApsAttendanceVo> apsAttendanceVoPage);
}




