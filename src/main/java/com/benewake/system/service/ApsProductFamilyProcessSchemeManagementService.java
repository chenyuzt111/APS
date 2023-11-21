package com.benewake.system.service;

import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.ProcessSchemeManagementParam;
import com.benewake.system.entity.vo.ProcessSchemeManagementVoPage;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_product_family_process_scheme_management】的数据库操作Service
* @createDate 2023-10-24 11:38:49
*/
public interface ApsProductFamilyProcessSchemeManagementService extends IService<ApsProductFamilyProcessSchemeManagement> {

    ProcessSchemeManagementVoPage getProcessSchemeManagement(Integer page, Integer size);

    Boolean setOrderNumber(ProcessSchemeManagementParam processSchemeManagementParam);

    void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam);
}
