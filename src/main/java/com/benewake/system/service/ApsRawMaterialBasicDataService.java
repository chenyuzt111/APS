package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.entity.vo.ApsRawMaterialBasicDataParam;
import com.benewake.system.entity.vo.ApsRawMaterialBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_raw_material_basic_data】的数据库操作Service
* @createDate 2023-12-04 09:27:44
*/
public interface ApsRawMaterialBasicDataService extends IService<ApsRawMaterialBasicData> ,ApsMasterDataBaseService{

    PageResultVo<ApsRawMaterialBasicDataVo> getRawMaterial(String name, Integer page, Integer size);

    boolean addOrUpdateRawMaterial(ApsRawMaterialBasicDataParam param);

    void downloadRawMaterial(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
