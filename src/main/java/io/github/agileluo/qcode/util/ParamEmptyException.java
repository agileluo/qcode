package io.github.agileluo.qcode.util;

import lombok.Data;

@Data
public class ParamEmptyException extends BizException {
	private static final long serialVersionUID = 1L;
	private String field;
	public ParamEmptyException(String field) {
		super(SystemErrorCode.NotNull.code, field + SystemErrorCode.NotNull.message);
		this.field = field;
	}
	
}
