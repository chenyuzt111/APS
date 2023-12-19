package com.benewake.system.service;

import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

public interface SchedulingResultService {
    List<Object> searchLike(SearchLikeParam searchLikeParam);

    void download(HttpServletResponse response, DownloadViewParams downloadParam);
}
