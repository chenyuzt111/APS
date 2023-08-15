package com.benewake.system.service.impl;

import com.benewake.system.entity.system.ApsCol;
import com.benewake.system.mapper.ApsColMapper;
import com.benewake.system.service.ApsColService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月14 16:16
 * 描 述： TODO
 */
@Service
public class ApsColServiceImpl implements ApsColService {
    @Autowired
    private ApsColMapper apsColMapper;

    @Override
    public List<ApsCol> getColsByMenuId(String menuId) {
        return apsColMapper.getColsByMenuId(menuId);
    }

    @Override
    public List<ApsCol> getColsByRoleAndMenu(String roleId, String menuId) {
        return apsColMapper.getColsByRoleAndMenu(roleId,menuId);
    }
}
