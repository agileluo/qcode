package io.github.agileluo.qcode.util;

public class ReqParamIllegalException extends BizException {
	private static final long serialVersionUID = 1L;
	private String field;
	private String msg;
	
	public ReqParamIllegalException(String field, String msg) {
		super(SystemErrorCode.ParamIllegal.code, field + " " + msg);
		this.field = field;
		this.msg = msg;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
