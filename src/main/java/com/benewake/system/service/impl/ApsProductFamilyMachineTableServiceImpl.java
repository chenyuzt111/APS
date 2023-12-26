package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.IFill;
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
import com.benewake.system.utils.BenewakeStringUtils;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.benewake.system.utils.BenewakeStringUtils.replaceAvailable;

/**
 * @author ASUS
 * @description 针对表【aps_product_family_machine_table】的数据库操作Service实现
 * @createDate 2023-10-31 10:48:29
 */
@Slf4j
@Service
public class ApsProductFamilyMachineTableServiceImpl extends ServiceImpl<ApsProductFamilyMachineTableMapper, ApsProductFamilyMachineTable>
        implements ApsProductFamilyMachineTableService {


    @Autowired
    private ApsProductFamilyMachineTableMapper familyMachineTableMapper;

    @Autowired
    private ProductFamilyMachineTablesVoToExcel productFamilyMachineTablesVoToExcel;

    @Autowired
    private ApsProcessNamePoolService processNamePoolService;

    @Autowired
    private ApsProductFamilyMachineTableMapper productFamilyMachineTableMapper;

    @Override
    public ApsProductFamilyMachineTablePageVo getApsMachineTable(String name, Integer page, Integer size) {
        Page<ApsProductFamilyMachineTableDto> tablePage = getApsProductFamilyMachineTableDtoPage(page, size);
        List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVos = getApsProductFamilyMachineTableVos(page, size, tablePage);
        ApsProductFamilyMachineTablePageVo apsProductFamilyMachineTablePageVo = new ApsProductFamilyMachineTablePageVo();
        apsProductFamilyMachineTablePageVo.setPage(tablePage);
        apsProductFamilyMachineTablePageVo.setApsProductFamilyMachineTables(apsProductFamilyMachineTableVos);
        return apsProductFamilyMachineTablePageVo;
    }

    private List<ApsProductFamilyMachineTableVo> getApsProductFamilyMachineTableVos(Integer page, Integer size, Page<ApsProductFamilyMachineTableDto> tablePage) {
        List<ApsProductFamilyMachineTableDto> tableDtos = tablePage.getRecords();
        List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVos = tableDtos.stream().map(x -> {
            ApsProductFamilyMachineTableVo ApsProductFamilyMachineTableVo = productFamilyMachineTableDtoToVo(x);
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

    private ApsProductFamilyMachineTableVo productFamilyMachineTableDtoToVo(ApsProductFamilyMachineTableDto dto) {
        ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo = new ApsProductFamilyMachineTableVo();
        apsProductFamilyMachineTableVo.setId(dto.getId());
//        apsProductFamilyMachineTableVo.setNumber(number);
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
            String unavailableDates = dto.getUnavailableDates();
            unavailableDates = unavailableDates.replaceAll(",", "\n");
            apsProductFamilyMachineTableVo.setExcelUnavailableDate(unavailableDates);

            List<String> list = Arrays.asList(dto.getUnavailableDates().split("\\s*,\\s*"));
            apsProductFamilyMachineTableVo.setUnavailableDates(list);

            LocalDateTime currentTime = LocalDateTime.now();
            boolean isInAnyRange = list.stream()
                    .map(range -> range.split(" to "))
                    .anyMatch(parts -> currentTime.isAfter(LocalDateTime.parse(parts[0], formatter))
                            && currentTime.isBefore(LocalDateTime.parse(parts[1], formatter)));
            apsProductFamilyMachineTableVo.setAvailable(isInAnyRange ? "否" : "是");
        }

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
            log.info("不可用事件！！！！" + unavailDate);
        } else {
            familyMachineTable.setUnavailableDates("");
        }
        return familyMachineTable;
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

    @Override
    public Page selectPageLists(Page<Object> page, QueryWrapper<Object> wrapper) {
        try {
            //这一段操作是将sql中关于是否可用字段的信息替换掉(数据库中保存的是否可用 是不正确的 因为他是计算出来的)
            if (wrapper != null) {
                Field expression = AbstractWrapper.class.getDeclaredField("expression");
                expression.setAccessible(true);
                MergeSegments o = (MergeSegments) expression.get(wrapper);
                String sqlSegment = o.getSqlSegment();
                String replaceAvailable = replaceAvailable(sqlSegment);
                Field sqlSegmentField = o.getClass().getDeclaredField("sqlSegment");
                sqlSegmentField.setAccessible(true);
                sqlSegmentField.set(o, replaceAvailable);
                sqlSegmentField.setAccessible(false);
                expression.set(wrapper, o);
                expression.setAccessible(false);
                //最大
                Integer count = productFamilyMachineTableMapper.selectCount(wrapper);
                page = new Page<>(1, count);
            }
            Page<ApsProductFamilyMachineTableDto> familyMachineTableDtoPage = productFamilyMachineTableMapper.selectPageLists(page, wrapper);
            List<ApsProductFamilyMachineTableDto> records = familyMachineTableDtoPage.getRecords();
            List<ApsProductFamilyMachineTableVo> productFamilyMachineTableVos = records.stream()
                    .map(this::productFamilyMachineTableDtoToVo)
                    .collect(Collectors.toList());
            return buildApsProductFamilyMachineTableVoPage(familyMachineTableDtoPage, productFamilyMachineTableVos);
        } catch (Exception e) {
            log.error("机器管理查询出错" + e.getMessage());
        }
        return null;
    }

    private Page<ApsProductFamilyMachineTableVo> buildApsProductFamilyMachineTableVoPage(Page<ApsProductFamilyMachineTableDto> familyMachineTableDtoPage, List<ApsProductFamilyMachineTableVo> productFamilyMachineTableVos) {
        Page<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTableVoPage = new Page<>();
        apsProductFamilyMachineTableVoPage.setRecords(productFamilyMachineTableVos);
        apsProductFamilyMachineTableVoPage.setTotal(familyMachineTableDtoPage.getTotal());
        apsProductFamilyMachineTableVoPage.setSize(familyMachineTableDtoPage.getSize());
        apsProductFamilyMachineTableVoPage.setCurrent(familyMachineTableDtoPage.getCurrent());
        return apsProductFamilyMachineTableVoPage;
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper) {
        return productFamilyMachineTableMapper.searchLike(queryWrapper);
    }
}




