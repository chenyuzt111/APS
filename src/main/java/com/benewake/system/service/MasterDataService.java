package com.benewake.system.service;

import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MasterDataService {
    ResultColPageVo<Object> getFiltrateDate(Integer page, Integer size, QueryViewParams queryViewParams);

    List<Object> searchLike(SearchLikeParam searchLikeParam);

    void download(HttpServletResponse response, DownloadViewParams downloadParam);
}
