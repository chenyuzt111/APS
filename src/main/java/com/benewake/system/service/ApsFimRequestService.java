package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsFimRequest;
import com.benewake.system.entity.vo.ApsFimRequestParam;
import com.benewake.system.entity.vo.ApsFimRequestVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_fim_request】的数据库操作Service
* @createDate 2023-12-01 09:34:02
*/
public interface ApsFimRequestService extends IService<ApsFimRequest> {

    PageResultVo<ApsFimRequestVo> getFimRequestPage(Integer page, Integer size);

    Boolean addOrUpdateFimRequest(ApsFimRequestParam fimRequestParam);

    void downloadFimRequest(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
