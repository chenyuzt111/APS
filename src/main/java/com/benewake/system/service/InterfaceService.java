package com.benewake.system.service;

import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface InterfaceService {

    PageResultVo<Object> getAllPage(Integer page, Integer size, Integer type);

    Boolean add(String request, Integer type);

    Boolean update(String request, Integer type);

    Boolean delete(List<Integer> ids, Integer type);

    void downloadInterfaceDate(HttpServletResponse response, DownloadViewParams downloadParam);

    void downloadInterfaceTemplate(Integer type, HttpServletResponse response);

    Boolean importInterfaceData(Integer code, Integer type, MultipartFile file);

    ResultColPageVo<Object> getPageFiltrate(Integer page, Integer size, QueryViewParams queryViewParams);

    List<Object> searchLike(SearchLikeParam searchLikeParam);
}
