package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.benewake.system.service.ApsPurchaseRequestsOrdersService;
import com.benewake.system.mapper.ApsPurchaseRequestsOrdersMapper;
import com.benewake.system.service.scheduling.kingdee.ApsPurchaseRequestsOrdersBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_purchase_requests_orders】的数据库操作Service实现
 * @createDate 2023-12-12 16:13:36
 */
@Service
public class ApsPurchaseRequestsOrdersServiceImpl extends ServiceImpl<ApsPurchaseRequestsOrdersMapper, ApsPurchaseRequestsOrders>
        implements ApsPurchaseRequestsOrdersService {

    @Autowired
    private List<ApsPurchaseRequestsOrdersBaseService> purchaseRequestsOrdersBaseServices;

    @Autowired
    private ApsPurchaseRequestsOrdersMapper purchaseRequestsOrdersMapper;

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = getMaxVersionIncr();
        List<ApsPurchaseRequestsOrders> purchaseRequestsOrderList = new ArrayList<>();
        for (ApsPurchaseRequestsOrdersBaseService service : purchaseRequestsOrdersBaseServices) {
            List<ApsPurchaseRequestsOrders> purchaseRequestsOrders = service.getKingdeeDates();
            if (CollectionUtils.isNotEmpty(purchaseRequestsOrders)) {
                purchaseRequestsOrderList.addAll(purchaseRequestsOrders);
            } else {
                ApsPurchaseRequestsOrders purchaseRequestsOrder = new ApsPurchaseRequestsOrders();
                purchaseRequestsOrder.setFormName(service.getServiceName());
                purchaseRequestsOrderList.add(purchaseRequestsOrder);
            }
        }
        purchaseRequestsOrderList = purchaseRequestsOrderList.stream().peek(x -> x.setVersion(maxVersionIncr)).collect(Collectors.toList());
        return saveBatch(purchaseRequestsOrderList);
    }

    @Override
    public void insertVersionIncr() {
        purchaseRequestsOrdersMapper.insertVersionIncr();
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        String customSqlSegment = wrapper.getCustomSqlSegment();
        if (StringUtils.isEmpty(customSqlSegment) || !customSqlSegment.contains("ORDER BY")) {
            wrapper.orderByDesc("ch_version_name");
            wrapper.orderByAsc("form_name");
            wrapper.orderByAsc("material_id");
            wrapper.orderByDesc("bill_no");
        }
        return purchaseRequestsOrdersMapper.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return purchaseRequestsOrdersMapper.searchLike(versionToChVersionArrayList, queryWrapper);
    }
}




