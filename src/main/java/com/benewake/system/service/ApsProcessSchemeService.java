package com.benewake.system.service;

import com.benewake.system.entity.ApsProcessScheme;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.*;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_process_scheme】的数据库操作Service
* @createDate 2023-10-21 17:38:05
*/
public interface ApsProcessSchemeService extends IService<ApsProcessScheme> {

    String saveProcessScheme(ApsProcessSchemeParams apsProcessSchemeParam);

    ApsProcessSchemeVoPage getProcessScheme(Integer page, Integer size);

    Boolean deleteProcessScheme(List<Integer> ids);

    ApsProcessSchemeByIdListVo getProcessSchemeById(Integer id);

    String updateProcessScheme(List<ApsProcessSchemeParam> apsProcessSchemeParam);

}