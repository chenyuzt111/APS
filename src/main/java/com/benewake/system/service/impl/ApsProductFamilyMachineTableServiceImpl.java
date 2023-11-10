package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.dto.ApsProductFamilyMachineTableDto;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import com.benewake.system.mapper.ApsProductFamilyMachineTableMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_product_family_machine_table】的数据库操作Service实现
 * @createDate 2023-10-31 10:48:29
 */
@Service
public class ApsProductFamilyMachineTableServiceImpl extends ServiceImpl<ApsProductFamilyMachineTableMapper, ApsProductFamilyMachineTable> implements ApsProductFamilyMachineTableService {

    @Override
    public ApsProductFamilyMachineTablePageVo getApsMachineTable(String name, Integer page, Integer size) {
        Page<ApsProductFamilyMachineTableDto> apsProductFamilyMachineTablePage = new Page<>();
        apsProductFamilyMachineTablePage.setCurrent(page);
        apsProductFamilyMachineTablePage.setSize(size);
        Page<ApsProductFamilyMachineTableDto> tablePage = baseMapper.getPage(apsProductFamilyMachineTablePage);
        List<ApsProductFamilyMachineTableDto> tableDtos = tablePage.getRecords();
        AtomicReference<Integer> number = new AtomicReference<>((page - 1) * size + 1);
        List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVos = tableDtos.stream().map(x -> {
            ApsProductFamilyMachineTableVo ApsProductFamilyMachineTableVo = productFamilyMachineTableDtoToVo(x, number.getAndSet(number.get() + 1));
            return ApsProductFamilyMachineTableVo;
        }).collect(Collectors.toList());
        ApsProductFamilyMachineTablePageVo apsProductFamilyMachineTablePageVo = new ApsProductFamilyMachineTablePageVo();
        apsProductFamilyMachineTablePageVo.setPage(tablePage);
        apsProductFamilyMachineTablePageVo.setApsProductFamilyMachineTables(apsProductFamilyMachineTableVos);
        return apsProductFamilyMachineTablePageVo;
    }

    private ApsProductFamilyMachineTableVo productFamilyMachineTableDtoToVo(ApsProductFamilyMachineTableDto dto, Integer number) {
        ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo = new ApsProductFamilyMachineTableVo();
        apsProductFamilyMachineTableVo.setId(dto.getId());
        apsProductFamilyMachineTableVo.setNumber(number);
        apsProductFamilyMachineTableVo.setFMachineId(dto.getFMachineId());
        apsProductFamilyMachineTableVo.setFMachineName(dto.getFMachineName());
        apsProductFamilyMachineTableVo.setFProductFamily(dto.getFProductFamily());
        apsProductFamilyMachineTableVo.setFProcessId(dto.getFProcessId());
        apsProductFamilyMachineTableVo.setProcessName(dto.getProcessName());
        apsProductFamilyMachineTableVo.setFMachineConfiguration(dto.getFMachineConfiguration());
        apsProductFamilyMachineTableVo.setFWorkshop(dto.getFWorkshop());
        apsProductFamilyMachineTableVo.setAvailable(dto.getAvailable());
        apsProductFamilyMachineTableVo.setUnavailableDates(dto.getUnavailableDates());
        apsProductFamilyMachineTableVo.setUnavailableTime(dto.getUnavailableTime());
        apsProductFamilyMachineTableVo.setVersion(dto.getVersion());
        return apsProductFamilyMachineTableVo;
    }

    @Override
    public boolean addOrUpdateApsMachineTable(ApsProductFamilyMachineTable apsProductFamilyMachineTable) {
        boolean res;
        if (apsProductFamilyMachineTable.getId() == null) {
            res = save(apsProductFamilyMachineTable);
        } else {
            res = updateById(apsProductFamilyMachineTable);
        }
        return res;
    }
}




