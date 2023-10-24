package com.benewake.system.entity.vo;

import lombok.Data;

@Data
public class SchedulingParam {
    Integer number_cycles;
    Integer scheduled_days_num;
    Integer scheduling_workload;
    Integer bach_size;
    Integer in_advance_po;
    Integer buy_delay_days;
    Integer yg_delta;
    Boolean produce_in_parallel;
    Boolean consider_the_material;
    Boolean consider_the_process;
    Boolean split_po_orders;
}
