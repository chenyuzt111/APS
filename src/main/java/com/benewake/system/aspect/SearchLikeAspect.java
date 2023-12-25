package com.benewake.system.aspect;

import com.benewake.system.annotation.SearchLike;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.redis.CacheConstants;
import com.benewake.system.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class SearchLikeAspect {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    @Around("@annotation(searchLike)")
    public Object doBefore(ProceedingJoinPoint point, SearchLike searchLike) throws Throwable {
        Object[] args = point.getArgs();
        SearchLikeParam searchLikeParams = (SearchLikeParam) args[0];
        Integer colId = searchLikeParams.getColId();
        String value = searchLikeParams.getValue();
        if (colId == null) {
            return Result.ok();
        }
        if (StringUtils.isNotEmpty(value)) {
            return point.proceed();
        }
        String key = CacheConstants.SEARCH_CONFIG + hostHolder.getUser().getUsername() + "::" + colId;
        Set<ZSetOperations.TypedTuple<String>> scoreWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 4);
        if (scoreWithScores == null || scoreWithScores.size() == 0) {
            return point.proceed();
        }
        Iterator<ZSetOperations.TypedTuple<String>> iterator = scoreWithScores.iterator();
        List<String> searchList = new ArrayList<>();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<String> next = iterator.next();
            if (next.getValue() != null) {
                searchList.add(next.getValue());
            }
        }
        // 这里返回List给到前端
        return Result.ok(searchList);
    }


}
