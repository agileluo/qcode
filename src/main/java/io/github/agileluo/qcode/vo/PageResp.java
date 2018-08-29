package io.github.agileluo.qcode.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResp<T> {
	private Integer total;
	private List<T> items;

	public static final boolean isNotEmpty(PageResp<?> data){
		if(data.getTotal() != null && data.getTotal() > 0){
			return true;
		}
		return false;
	}
}
