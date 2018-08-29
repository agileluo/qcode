package io.github.agileluo.qcode.util;

import lombok.Data;

/**
 *
 * 业务异常，异常信息message为前端可显示信息
 * @author luoml
 *
 */
@Data
public class BizException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;

	public BizException(String code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	public BizException(String message) {
		super(message);
		this.code = "0000";
		this.message = message;
	}
	public BizException(SystemErrorCode code){
		super(code.message);
		this.code = code.code;
		this.message = code.message;
	}
}
