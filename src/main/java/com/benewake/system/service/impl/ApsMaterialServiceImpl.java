package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.service.ApsMaterialService;
import com.benewake.system.mapper.ApsMaterialMapper;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_material】的数据库操作Service实现
* @createDate 2023-12-11 18:04:36
*/
@Service
public class ApsMaterialServiceImpl extends ServiceImpl<ApsMaterialMapper, ApsMaterial>
    implements ApsMaterialService {

    @Autowired
    private List<ApsMaterialBaseService> baseMaterialServices;
    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = getMaxVersionIncr();
        List<ApsMaterial> apsMaterials = new ArrayList<>();
        for (ApsMaterialBaseService baseMaterialService : baseMaterialServices) {
            List<ApsMaterial> kingdeeDates = baseMaterialService.getKingdeeDates();
            apsMaterials.addAll(kingdeeDates);
        }
        for (ApsMaterial apsMaterial : apsMaterials) {
            apsMaterial.setVersion(maxVersionIncr);
        }
        return saveBatch(apsMaterials);
    }

    @Override
    public void insertVersionIncr() {
        ApsMaterialService.super.insertVersionIncr();
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        return ApsMaterialService.super.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        return ApsMaterialService.super.selectPageList(page, tableVersionList);
    }
}




