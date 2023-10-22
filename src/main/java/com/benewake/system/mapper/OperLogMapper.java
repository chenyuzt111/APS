package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.SysOperLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Lcs
 * @since 2023年08月04 15:13
 * 描 述： TOD
 */
@Mapper
@Repository
public interface OperLogMapper extends BaseMapper<SysOperLog> {
}
