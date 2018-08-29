package io.github.agileluo.qcode.vo;

import lombok.Data;

@Data
public class PageReq<T> {
	private Integer page;
	private Integer pageSize;
	private T data;
}
