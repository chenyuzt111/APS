package com.benewake.system.utils;

import com.benewake.system.exception.BeneWakeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {
    public static void validateHeadMap(Map<Integer, String> headMap, List<String> head) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < head.size(); i++) {
            String h = headMap.get(i);
            if (h == null || !h.equals(head.get(i))) {
                res.append("第").append(i + 1).append("列的列名不符合条件，应改为:").append(head.get(i)).append("\n");
            }
        }
        if (BenewakeStringUtils.isNotEmpty(res.toString())) {
            log.warn("导入列名不正确" + res + new Date());
            throw new BeneWakeException(res.toString());
        }
    }

}
