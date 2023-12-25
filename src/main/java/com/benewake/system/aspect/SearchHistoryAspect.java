package com.benewake.system.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.system.annotation.Scheduling;
import com.benewake.system.annotation.SearchHistory;
import com.benewake.system.entity.ApsSearchHistory;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.redis.CacheConstants;
import com.benewake.system.service.ApsSearchHistoryService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class SearchHistoryAspect {

    @Autowired
    private ApsSearchHistoryService searchHistoryService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    @Around("@annotation(searchHistory)")
    public Object doBefore(ProceedingJoinPoint point, SearchHistory searchHistory) throws Throwable {
        Object[] args = point.getArgs();
        List<Object> queryViewParamList = Arrays.stream(args)
                .filter(x -> x instanceof QueryViewParams)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(queryViewParamList)) {
            return point.proceed();
        }
        QueryViewParams queryViewParams = (QueryViewParams) queryViewParamList.get(0);
        List<ViewColParam> cols = queryViewParams.getCols();

        if (CollectionUtils.isEmpty(cols)) {
            return point.proceed();
        }

        String username = hostHolder.getUser().getUsername();
        Map<String, Set<String>> userSearchHistory = new HashMap<>();

        cols.forEach(x -> {
            Integer colId = x.getColId();
            String colValue = x.getColValue();
            String key = CacheConstants.SEARCH_CONFIG + username + "::" + colId;
            userSearchHistory
                    .computeIfAbsent(key, k -> new HashSet<>()).add(colValue);
        });

        // 批量写入Redis
        userSearchHistory.forEach((key, values) -> {
            values.forEach(value -> {
                        if (value != null) {
                            insertSearchSort(key, value);
                        }
                    }
            );
        });

        return point.proceed();
    }

    public void insertSearchSort(String key, String value) {
        // 阈值-历史最多10个
        long top = 5;
        // 拿到存入Redis里数据的唯一分值
        Double score = redisTemplate.opsForZSet().score(key, value);

        // 检索是否有旧记录  1.无则插入记录值  2.有则删除 再次插入
        if (null != score) {
            // 删除旧的
            redisTemplate.opsForZSet().remove(key, value);
        }
        // 加入新的记录，设置当前时间戳为分数score
        redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
        // 获取总记录数
        Long aLong = redisTemplate.opsForZSet().zCard(key);
        if (aLong == null) {
            return;
        }
        if (aLong > top) {
            // 获取阈值200之后的记录  (0,1] 并移除
            redisTemplate.opsForZSet().removeRange(key, 0, aLong - top - 1);
        }
    }


}
