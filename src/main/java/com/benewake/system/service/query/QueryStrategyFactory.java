package com.benewake.system.service.query;

import com.benewake.system.service.query.strategy.*;

import java.util.HashMap;
import java.util.Map;

public class QueryStrategyFactory {
    private final Map<String, QueryStrategy> strategyMap = new HashMap<>();

    public QueryStrategyFactory() {
        strategyMap.put("like", new LikeStrategy());
        strategyMap.put("notlike", new NotLikeStrategy());
        strategyMap.put("gt", new GreaterThanStrategy());
        strategyMap.put("ge", new GreaterThanOrEqualStrategy());
        strategyMap.put("eq", new EqualStrategy());
        strategyMap.put("ne", new NotEqualStrategy());
        strategyMap.put("lt", new LessThanStrategy());
        strategyMap.put("le", new LessThanOrEqualStrategy());
        strategyMap.put("null", new IsNullStrategy());
        strategyMap.put("notnull", new IsNotNullStrategy());
        strategyMap.put("ascending", new AscStrategy());
        strategyMap.put("descending", new DescStrategy());
    }

    public QueryStrategy getStrategy(String valueOperator) {
        return strategyMap.get(valueOperator);
    }
}
