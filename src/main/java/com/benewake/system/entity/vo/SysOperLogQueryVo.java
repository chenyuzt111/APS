package com.benewake.system.entity.vo;

import lombok.Data;

/**
 * @author Lcs
 */
@Data
public class SysOperLogQueryVo {

	private String title;
	private String operName;

	private String createTimeBegin;
	private String createTimeEnd;

}

