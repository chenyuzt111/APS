package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsFimRequest;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_fim_request】的数据库操作Service
 * @createDate 2023-12-01 09:34:02
 */
public interface ApsFimRequestService extends IService<ApsFimRequest>,ApsIntfaceDataServiceBase {

    PageResultVo<ApsFimRequestVo> getFimRequestPage(Integer page, Integer size);

    Boolean addOrUpdateFimRequest(ApsFimRequestParam fimRequestParam);


    Boolean saveDataByExcel(Integer type, MultipartFile file);

}
