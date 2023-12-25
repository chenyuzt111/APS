package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsAttendance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsAttendanceDto;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_attendance】的数据库操作Mapper
 * @createDate 2023-12-05 15:56:08
 * @Entity com.benewake.system.entity.ApsAttendance
 */
@Mapper
public interface ApsAttendanceMapper extends BaseMapper<ApsAttendance> {

    Page<ApsAttendanceDto> getAttendancePage(Page<ApsAttendanceDto> apsAttendanceVoPage);

    Page<ApsAttendanceVo> selectPageLists(Page<Object> page,
                                          @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper);
}