package com.benewake.system.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ViewTableListVo {

    private List<ViewTableVo> viewTableVos;

    private Integer defaultViewId;

}
