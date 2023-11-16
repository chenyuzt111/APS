package com.benewake.system.service;

import com.benewake.system.entity.ApsProcessNamePool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsProcessNamePoolPageVo;
import com.benewake.system.entity.vo.DownloadParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_process_name_pool】的数据库操作Service
* @createDate 2023-10-20 09:20:16
*/
public interface ApsProcessNamePoolService extends IService<ApsProcessNamePool> {

    Boolean addOrUpdateProcess(ApsProcessNamePool apsProcessNamePool);

    ApsProcessNamePoolPageVo getProcess(String name, Integer page, Integer size);

    void downloadProceeName(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
