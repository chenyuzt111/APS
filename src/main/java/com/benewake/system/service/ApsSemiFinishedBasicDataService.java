package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsSemiFinishedBasicData;
import com.benewake.system.entity.vo.ApsSemiFinishedBasicDataParam;
import com.benewake.system.entity.vo.ApsSemiFinishedBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_basic_data】的数据库操作Service
* @createDate 2023-12-04 13:35:18
*/
public interface ApsSemiFinishedBasicDataService extends IService<ApsSemiFinishedBasicData> {

    PageResultVo<ApsSemiFinishedBasicDataVo> getSemiFinished(String name, Integer page, Integer size);

    boolean addOrUpdateSemiFinished(ApsSemiFinishedBasicDataParam param);

    void downloadSemiFinished(HttpServletResponse response, DownloadParam downloadParam);
}
