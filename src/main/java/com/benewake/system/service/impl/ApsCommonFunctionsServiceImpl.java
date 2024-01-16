package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsCommonFunctions;
import com.benewake.system.entity.vo.ApsCommonFunctionsVo;
import com.benewake.system.service.ApsCommonFunctionsService;
import com.benewake.system.mapper.ApsCommonFunctionsMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 10451
* @description 针对表【aps_common_functions】的数据库操作Service实现
* @createDate 2024-01-03 10:39:46
*/
@Service
public class ApsCommonFunctionsServiceImpl extends ServiceImpl<ApsCommonFunctionsMapper, ApsCommonFunctions>
    implements ApsCommonFunctionsService{

    private final ApsCommonFunctionsMapper apsCommonFunctionsMapper;

    public ApsCommonFunctionsServiceImpl(ApsCommonFunctionsMapper apsCommonFunctionsMapper) {
        this.apsCommonFunctionsMapper = apsCommonFunctionsMapper;
    }

    @Override
    public List<ApsCommonFunctionsVo> getCommonFunctionsByUserId(Integer userId) {
         List<ApsCommonFunctions> apsCommonFunctionsDtos = apsCommonFunctionsMapper.getCommonFunctionsByUserId(userId);
        List<ApsCommonFunctionsVo> apsCommonFunctionsVos = new ArrayList<>(); // 初始化ArrayList

         for (ApsCommonFunctions apsCommonFunctionsDto : apsCommonFunctionsDtos){
             ApsCommonFunctionsVo apsCommonFunctionsVo = new ApsCommonFunctionsVo();
             apsCommonFunctionsVo.setLabel(apsCommonFunctionsDto.getLabel());
             apsCommonFunctionsVo.setName(apsCommonFunctionsDto.getName());
             apsCommonFunctionsVo.setPath(apsCommonFunctionsDto.getPath());
             apsCommonFunctionsVos.add(apsCommonFunctionsVo);
         }
        return apsCommonFunctionsVos;
    }

    @Override
    public void updateCommonFunctions(List<ApsCommonFunctionsVo> apsCommonFunctionVos, int userId) {
        // 删除旧记录
        apsCommonFunctionsMapper.deleteCommonFunctionsByUserId(userId);

        // 插入新记录
        List<ApsCommonFunctions> apsCommonFunctions = new ArrayList<>();
        if (apsCommonFunctionVos != null && !apsCommonFunctionVos.isEmpty()) {
            for (ApsCommonFunctionsVo apsCommonFunctionsVo : apsCommonFunctionVos){
                ApsCommonFunctions apsCommonFunction = new ApsCommonFunctions();
                apsCommonFunction.setPath(apsCommonFunctionsVo.getPath());
                apsCommonFunction.setName(apsCommonFunctionsVo.getName());
                apsCommonFunction.setLabel(apsCommonFunctionsVo.getLabel());
                apsCommonFunction.setUserId(userId);
                apsCommonFunctions.add(apsCommonFunction);
            }
            apsCommonFunctionsMapper.insertCommonFunctions(apsCommonFunctions);
        }
    }


}




