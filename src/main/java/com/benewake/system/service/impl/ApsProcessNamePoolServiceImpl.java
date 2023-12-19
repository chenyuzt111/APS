package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import com.benewake.system.excel.entity.ExcelProcessNamePoolTemplate;
import com.benewake.system.excel.listener.ProcessPoolListener;
import com.benewake.system.excel.transfer.ProcessNamePoolDtoToExcelList;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessNamePoolMapper;
import com.benewake.system.service.ApsProcessNamePoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_process_name_pool】的数据库操作Service实现
 * @createDate 2023-10-20 09:20:16
 */
@Slf4j
@Service
public class ApsProcessNamePoolServiceImpl extends ServiceImpl<ApsProcessNamePoolMapper, ApsProcessNamePool>
        implements ApsProcessNamePoolService {

    @Autowired
    private ProcessNamePoolDtoToExcelList processNamePoolDtoToExcelList;

    @Autowired
    private ApsProcessNamePoolMapper processNamePoolMapper;

    @Override
    public Boolean addOrUpdateProcess(ApsProcessNamePool apsProcessNamePool) {
        String processName = apsProcessNamePool.getProcessName();
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<ApsProcessNamePool>()
                .eq(ApsProcessNamePool::getProcessName, processName);
        ApsProcessNamePool processNamePool = this.getOne(apsProcessNamePoolLambdaQueryWrapper);
        if (processNamePool != null) {
            throw new BeneWakeException("该名称已经存在");
        }
        if (apsProcessNamePool.getId() != null) {
            return this.updateById(apsProcessNamePool);
        }
        return this.save(apsProcessNamePool);
    }

    @Override
    public ApsProcessNamePoolPageVo getProcess(String name, Integer page, Integer size) {
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            apsProcessNamePoolLambdaQueryWrapper.like(ApsProcessNamePool::getProcessName, name);
        }
        Page<ApsProcessNamePool> apsProcessNamePoolPage = new Page<>();
        apsProcessNamePoolPage.setSize(size);
        apsProcessNamePoolPage.setCurrent(page);
        Page<ApsProcessNamePool> apsProcessNamePools = this.page(apsProcessNamePoolPage, apsProcessNamePoolLambdaQueryWrapper);
        ApsProcessNamePoolPageVo apsProcessNamePoolPageVo = new ApsProcessNamePoolPageVo();
        List<ApsProcessNamePool> records = apsProcessNamePools.getRecords();
        AtomicReference<Integer> number = new AtomicReference<>((page - 1) * size + 1);
        List<ApsProcessNamePoolVo> processNamePools = records.stream().map(x -> {
            ApsProcessNamePoolVo apsProcessNamePoolVo = new ApsProcessNamePoolVo();
            apsProcessNamePoolVo.setProcessName(x.getProcessName());
            apsProcessNamePoolVo.setId(x.getId());
//            apsProcessNamePoolVo.setNumber(number.getAndSet(number.get() + 1));
            return apsProcessNamePoolVo;
        }).collect(Collectors.toList());
        apsProcessNamePoolPageVo.setApsProcessNamePools(processNamePools);
        apsProcessNamePoolPageVo.setPage(page);
        apsProcessNamePoolPageVo.setSize(size);
        apsProcessNamePoolPageVo.setTotal(apsProcessNamePoolPage.getTotal());
        apsProcessNamePoolPageVo.setPages(apsProcessNamePoolPage.getPages());
        return apsProcessNamePoolPageVo;
    }

    @Override
    public void downloadProceeName(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("我是文件名", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<ApsProcessNamePool> apsProcessNamePools = null;
            if (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()) {
                Page<ApsProcessNamePool> poolPage = new Page<>();
                poolPage.setSize(downloadParam.getSize());
                poolPage.setCurrent(downloadParam.getPage());
                apsProcessNamePools = page(poolPage).getRecords();
            } else {
                apsProcessNamePools = getBaseMapper().selectList(null);
            }
            List<ExcelProcessNamePool> excelProcessNamePools = processNamePoolDtoToExcelList.convert(apsProcessNamePools);
            EasyExcel.write(response.getOutputStream(), ExcelProcessNamePool.class).sheet("模板").doWrite(excelProcessNamePools);
        } catch (Exception e) {
            log.error("工序命名池导出失败了" + e);
            throw new BeneWakeException("工序命名池导出失败了");
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),
                            ExcelProcessNamePoolTemplate.class, new ProcessPoolListener(this, type))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            log.error("工序与产能导入失败" + e.getMessage());
            throw new BeneWakeException(e.getMessage());
        }
        return true;
    }

    @Override
    public Page selectPageLists(Page<Object> page, QueryWrapper<Object> wrapper) {
        Page<ApsProcessNamePoolVo> processNames = processNamePoolMapper.selectPages(page, wrapper);
        long size = page.getSize();
        long current = page.getCurrent();
        AtomicLong number = new AtomicLong((current - 1) * size + 1);
        processNames.getRecords().forEach(x -> {
            x.setNumber(number.getAndIncrement());
        });
        return processNames;
    }
}




