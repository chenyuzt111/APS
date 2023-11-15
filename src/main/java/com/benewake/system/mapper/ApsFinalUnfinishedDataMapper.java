package com.benewake.system.mapper;

import com.benewake.system.entity.ApsFinalUnfinishedData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
* @author ASUS
* @description 针对表【aps_final_unfinished_data】的数据库操作Mapper
* @createDate 2023-11-15 15:33:25
* @Entity com.benewake.system.entity.ApsFinalUnfinishedData
*/
public interface ApsFinalUnfinishedDataMapper extends BaseMapper<ApsFinalUnfinishedData> {

    @Update("call UpdateApsFinalUnfinishedData()")
    void callUpdateApsFinalUnfinishedData();
}




