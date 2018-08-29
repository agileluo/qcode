package io.github.agileluo.qcode.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by liuwei on 16-5-19.
 */
public class ResponseVo {
    public static final String success_result = "success";
    public static final String error_result = "error";

    private String result;
    private String errorMsg;
    private String errorCode;
    private Object content;

    public ResponseVo() {
        this.result = success_result;
    }
    public ResponseVo(String result) {
        if (result == null)
            result = success_result;
        this.result = result;
    }
    public ResponseVo(Object content) {
        this.result = success_result;
        this.content = content;
    }

    public ResponseVo(String errorMsg, String errorCode) {
        this.result = error_result;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public ResponseVo(String errorMsg, String errorCode, String result) {
        this.result = result;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

  public static ResponseVo success() {
    ResponseVo result = new ResponseVo();
    return result;
  }

  public static ResponseVo success(Object content) {
    ResponseVo result = new ResponseVo();
    result.setContent(content);
    return result;
  }

  public static ResponseVo error() {
    return new ResponseVo(error_result);
  }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
