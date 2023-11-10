package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterialNameMapping;
import com.benewake.system.entity.kingdee.KingdeeDataSource;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsMaterialNameMappingMapper;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialNameMappingService;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author ASUS
* @description 针对表【aps_data_source】的数据库操作Service实现
* @createDate 2023-11-09 11:42:15
*/
@Service
public class ApsMaterialNameMappingServiceImpl extends ServiceImpl<ApsMaterialNameMappingMapper, ApsMaterialNameMapping>
    implements ApsMaterialNameMappingService {
}




