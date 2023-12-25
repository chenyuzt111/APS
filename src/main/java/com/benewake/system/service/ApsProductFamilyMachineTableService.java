package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableParam;
import com.benewake.system.entity.vo.DownloadParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_product_family_machine_table】的数据库操作Service
* @createDate 2023-10-31 10:48:29
*/
public interface ApsProductFamilyMachineTableService extends IService<ApsProductFamilyMachineTable> ,ApsMasterDataBaseService{

    ApsProductFamilyMachineTablePageVo getApsMachineTable(String name, Integer page, Integer size);

    boolean addOrUpdateApsMachineTable(ApsProductFamilyMachineTableParam apsProductFamilyMachineTable);

    void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam);

    Boolean saveDataByExcel(Integer type, MultipartFile file);
}
