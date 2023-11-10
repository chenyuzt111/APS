package com.benewake.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.exception.BeneWakeException;

import java.lang.reflect.Field;
import java.util.List;

public interface ApsIntfaceDataServiceBase<T> {

    String accessToken = "a08dd535-f367-4797-a185-1b57435dcdd1"; // 替换为实际的accessToken

    Boolean updateDataVersions() throws Exception;

    default Integer getMaxVersionIncr() {
        try {
            QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.orderByDesc("version")
                    .last("limit 1");
            IService apsImmediatelyInventoryServices = (IService) this;
            Object one = apsImmediatelyInventoryServices.getOne(objectQueryWrapper);
            Integer ver = 0;
            if (one != null) {
                Field[] fields = one.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getName().equals("version")) {
                        ver = (Integer) field.get(one);
                        break;
                    }
                    field.setAccessible(false);
                }
            }
            return ver == null ? 2 : ver + 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeneWakeException("系统内部错误 请抓紧联系相关人员");
        }
    }

    List<Object> selectVersionPageList(Integer pass, Integer size, List<VersionToChVersion> versionToChVersionArrayList);

    default Page selectPageList(Page<Object> objectPage, List<Integer> tableVersionList) {
        throw new BeneWakeException("该表不能通过该方式查询");
    }

    default void insertVersionIncr() {
        return;
    }
}
