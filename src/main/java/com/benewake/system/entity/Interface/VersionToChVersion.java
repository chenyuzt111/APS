package com.benewake.system.entity.Interface;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionToChVersion {
    private Integer version;
    private String chVersionName;
}
