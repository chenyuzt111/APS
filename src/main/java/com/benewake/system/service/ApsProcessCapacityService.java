package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessCapacityParam;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.entity.vo.DownloadParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_process_capacity(工序与产能表)】的数据库操作Service
* @createDate 2023-10-20 15:03:52
*/
public interface ApsProcessCapacityService extends IService<ApsProcessCapacity> ,ApsMasterDataBaseService{

    Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityParam apsProcessCapacityVo);

    ApsProcessCapacityListVo getAllProcessCapacity(Integer page, Integer size);

    List<ApsProcessCapacityVo> getProcessCapacitysByproductFamily(String productFamily);

    boolean removeBatchAndUpdateByIds(List<Integer> ids);

    Boolean updateProcessNumber(List<ApsProcessCapacityParam> apsProcessCapacityVo);

    void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
