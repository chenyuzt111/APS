package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.vo.ApsFinishedProductBasicDataParam;
import com.benewake.system.entity.vo.ApsFinishedProductBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ASUS
 * @description 针对表【aps_finished_product_basic_data】的数据库操作Service
 * @createDate 2023-10-20 11:27:46
 */
public interface ApsFinishedProductBasicDataService extends IService<ApsFinishedProductBasicData>, ApsMasterDataBaseService {

    PageResultVo<ApsFinishedProductBasicDataVo> getFinishedProduct(String name, Integer page, Integer size);

    boolean addOrUpdateFinishedProduct(ApsFinishedProductBasicDataParam param);

    void downloadFinishedProduct(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
