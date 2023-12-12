package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOrder;
import com.benewake.system.service.ApsOrderService;
import com.benewake.system.mapper.ApsOrderMapper;
import com.benewake.system.service.scheduling.kingdee.ApsOrderBaseService;
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

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = getMaxVersionIncr();
        List<ApsOrder> res = new ArrayList<>();
        for (ApsOrderBaseService orderBaseService : orderBaseServices) {
            List<ApsOrder> apsOrders = orderBaseService.getKingdeeDates();
            res.addAll(apsOrders);
        }
        res = res.stream().peek(x -> x.setVersion(maxVersionIncr)).collect(Collectors.toList());
        return saveBatch(res);
    }
}




