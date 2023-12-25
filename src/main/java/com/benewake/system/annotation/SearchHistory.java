package com.benewake.system.annotation;

import com.benewake.system.entity.enums.TableVersionState;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchHistory {

}
