package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOrder;
import com.benewake.system.service.ApsOrderService;
import com.benewake.system.mapper.ApsOrderMapper;
import com.benewake.system.service.scheduling.kingdee.ApsOrderBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_order】的数据库操作Service实现
 * @createDate 2023-12-12 11:24:53
 */
@Service
public class ApsOrderServiceImpl extends ServiceImpl<ApsOrderMapper, ApsOrder>
        implements ApsOrderService {

    @Autowired
    private List<ApsOrderBaseService> orderBaseServices;

    @Autowired
    private ApsOrderMapper apsOrderMapper;

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = getMaxVersionIncr();
        List<ApsOrder> res = new ArrayList<>();
        for (ApsOrderBaseService orderBaseService : orderBaseServices) {
            List<ApsOrder> apsOrders = orderBaseService.getKingdeeDates();
            if (CollectionUtils.isNotEmpty(apsOrders)) {
                res.addAll(apsOrders);
            } else {
                ApsOrder apsOrder = new ApsOrder();
                apsOrder.setFormName(orderBaseService.getServiceName());
                res.addAll(apsOrders);
            }
        }
        res = res.stream().peek(x -> x.setVersion(maxVersionIncr)).collect(Collectors.toList());
        return saveBatch(res);
    }


    @Override
    public void insertVersionIncr() {
        apsOrderMapper.insertVersionIncr();
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        return apsOrderMapper.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return apsOrderMapper.searchLike(versionToChVersionArrayList, queryWrapper);
    }
}




