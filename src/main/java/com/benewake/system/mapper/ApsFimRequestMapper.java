package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsFimRequest;
import com.benewake.system.entity.vo.ApsFimRequestVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ASUS
* @description 针对表【aps_fim_request】的数据库操作Mapper
* @createDate 2023-12-01 09:34:02
* @Entity com.benewake.system.entity.ApsFimRequest
*/
@Mapper
public interface ApsFimRequestMapper extends BaseMapper<ApsFimRequest> {

    Page<ApsFimRequestVo> getFimRequestPage(Page<ApsFimRequestVo> pageParam);
}




