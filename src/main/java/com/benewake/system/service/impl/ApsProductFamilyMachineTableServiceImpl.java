package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.dto.ApsProductFamilyMachineTableDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableParam;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.excel.entity.ExcelMachineTableTemplate;
import com.benewake.system.excel.entity.ExcelProductFamilyMachineTable;
import com.benewake.system.excel.listener.MachineTableListener;
import com.benewake.system.excel.transfer.ProductFamilyMachineTablesVoToExcel;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProductFamilyMachineTableMapper;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import com.benewake.system.utils.ResponseUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
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


    @Autowired
    private ApsProductFamilyMachineTableMapper familyMachineTableMapper;

    @Autowired
    private ProductFamilyMachineTablesVoToExcel productFamilyMachineTablesVoToExcel;

    @Autowired
    private ApsProcessNamePoolService processNamePoolService;

    private LocalDateTime currentTime = null;

    @Override
    public ApsProductFamilyMachineTablePageVo getApsMachineTable(String name, Integer page, Integer size) {
        currentTime = LocalDateTime.now();
        Page<ApsProductFamilyMachineTableDto> tablePage = getApsProductFamilyMachineTableDtoPage(page, size);
        List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVos = getApsProductFamilyMachineTableVos(page, size, tablePage);
        ApsProductFamilyMachineTablePageVo apsProductFamilyMachineTablePageVo = new ApsProductFamilyMachineTablePageVo();
        apsProductFamilyMachineTablePageVo.setPage(tablePage);
        apsProductFamilyMachineTablePageVo.setApsProductFamilyMachineTables(apsProductFamilyMachineTableVos);
        return apsProductFamilyMachineTablePageVo;
    }

    private List<ApsProductFamilyMachineTableVo> getApsProductFamilyMachineTableVos(Integer page, Integer size, Page<ApsProductFamilyMachineTableDto> tablePage) {
        List<ApsProductFamilyMachineTableDto> tableDtos = tablePage.getRecords();
        AtomicReference<Integer> number = new AtomicReference<>((page - 1) * size + 1);
        List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVos = tableDtos.stream().map(x -> {
            ApsProductFamilyMachineTableVo ApsProductFamilyMachineTableVo = productFamilyMachineTableDtoToVo(x, number.getAndSet(number.get() + 1));
            return ApsProductFamilyMachineTableVo;
        }).collect(Collectors.toList());
        return apsProductFamilyMachineTableVos;
    }

    private Page<ApsProductFamilyMachineTableDto> getApsProductFamilyMachineTableDtoPage(Integer page, Integer size) {
        Page<ApsProductFamilyMachineTableDto> apsProductFamilyMachineTablePage = new Page<>();
        apsProductFamilyMachineTablePage.setCurrent(page);
        apsProductFamilyMachineTablePage.setSize(size);
        Page<ApsProductFamilyMachineTableDto> tablePage = baseMapper.getPage(apsProductFamilyMachineTablePage);
        return tablePage;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isEmpty(dto.getUnavailableDates())) {
            apsProductFamilyMachineTableVo.setUnavailableDates(Collections.emptyList());
            apsProductFamilyMachineTableVo.setAvailable("是");
        } else {
            List<String> list = Arrays.asList(dto.getUnavailableDates().split("\\s*,\\s*"));
            apsProductFamilyMachineTableVo.setUnavailableDates(list);

            LocalDateTime currentTime = LocalDateTime.now();
            boolean isInAnyRange = list.stream()
                    .map(range -> range.split(" to "))
                    .anyMatch(parts -> currentTime.isAfter(LocalDateTime.parse(parts[0], formatter))
                            && currentTime.isBefore(LocalDateTime.parse(parts[1], formatter)));
            apsProductFamilyMachineTableVo.setAvailable(isInAnyRange ? "否" : "是");
        }

        apsProductFamilyMachineTableVo.setVersion(dto.getVersion());
        return apsProductFamilyMachineTableVo;
    }


    @Override
    public boolean addOrUpdateApsMachineTable(ApsProductFamilyMachineTableParam apsProductFamilyMachineTable) {
        boolean res;
        ApsProductFamilyMachineTable familyMachineTable = getMachineParamToPo(apsProductFamilyMachineTable);
        if (apsProductFamilyMachineTable.getId() == null) {
            res = save(familyMachineTable);
        } else {
            res = updateById(familyMachineTable);
        }
        return res;
    }

    private ApsProductFamilyMachineTable getMachineParamToPo(ApsProductFamilyMachineTableParam apsProductFamilyMachineTable) {
        ApsProductFamilyMachineTable familyMachineTable = new ApsProductFamilyMachineTable();
        familyMachineTable.setId(apsProductFamilyMachineTable.getId());
        familyMachineTable.setFMachineId(apsProductFamilyMachineTable.getFMachineId());
        familyMachineTable.setFMachineName(apsProductFamilyMachineTable.getFMachineName());
        familyMachineTable.setFProductFamily(apsProductFamilyMachineTable.getFProductFamily());
        familyMachineTable.setFProcessId(apsProductFamilyMachineTable.getFProcessId());
        familyMachineTable.setFMachineConfiguration(apsProductFamilyMachineTable.getFMachineConfiguration());
        familyMachineTable.setFWorkshop(apsProductFamilyMachineTable.getFWorkshop());
        familyMachineTable.setAvailable(apsProductFamilyMachineTable.getAvailable());
        List<String> unavailableDates = apsProductFamilyMachineTable.getUnavailableDates();
        if (CollectionUtils.isNotEmpty(unavailableDates)) {
            String unavailDate = String.join(",", unavailableDates);
            familyMachineTable.setUnavailableDates(unavailDate);
            log.warn("不可用事件！！！！" + unavailDate);
        } else {
            familyMachineTable.setUnavailableDates("");
        }
        return familyMachineTable;
    }

    @Override
    public void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "机器管理");
            ApsProductFamilyMachineTablePageVo apsMachineTable = null;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Long count = familyMachineTableMapper.selectCount(null);
                apsMachineTable = getApsMachineTable(null, 1, Math.toIntExact(count));
            } else {
                apsMachineTable = getApsMachineTable(null, downloadParam.getPage(), downloadParam.getSize());
            }
            if (apsMachineTable == null || CollectionUtils.isEmpty(apsMachineTable.getApsProductFamilyMachineTables())) {
                throw new BeneWakeException("当前还没有机器信息");
            }
            List<ApsProductFamilyMachineTableVo> machineTables = apsMachineTable.getApsProductFamilyMachineTables();
            List<ExcelProductFamilyMachineTable> excelProductFamilyMachineTables = productFamilyMachineTablesVoToExcel.convert(machineTables);
            EasyExcel.write(response.getOutputStream(), ExcelProductFamilyMachineTable.class)
                    .sheet("sheet1").doWrite(excelProductFamilyMachineTables);
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                log.error("机器导出出错----" + LocalDateTime.now() + "----" + e.getMessage());
                throw new BeneWakeException("机器导出出错");
            }
            throw new BeneWakeException(e.getMessage());
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelMachineTableTemplate.class,
                            new MachineTableListener(this, processNamePoolService, type))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            log.error("保存机器表出错" + e.getMessage());
            return false;
        }
        return true;
    }
}




