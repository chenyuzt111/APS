package com.benewake.system.service;

import com.benewake.system.entity.ApsViewTable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.entity.vo.ViewTableListVo;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_view_table】的数据库操作Service
* @createDate 2023-12-06 15:29:02
*/
public interface ApsViewTableService extends IService<ApsViewTable> {

    ViewTableListVo getViews(Integer tableId);

    Boolean saveView(ViewParam viewParam);
}
