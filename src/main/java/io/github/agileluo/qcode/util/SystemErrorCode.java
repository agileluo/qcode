package io.github.agileluo.qcode.util;

/**
 * 系统异常
 * @author luoml
 *
 */
public enum SystemErrorCode {

  SystemError("0001", "系统异常"),
  NotNull("0001", "参数不能为空"),
  ParamIllegal("0002", "请求参数异常"),
  NoMethod("0003", "未找到相应接口"),
  Positive("0004", "必须为正数"),
  RequestParamIllegal("0005", "请求参数异常"),
  BusinessProcessing("0006", "业务处理中"),
  NeedLogin("0007", "未登录"),
  MQMessageSendError("0008", "MQ消息发送异常"),
  DaoGetTooMush("0009", "返回太多数据"),
  UpdateBusKeyCannotNull("0010", "更新数据业务主键不能为空"),
  DataNotFund("0011", "记录不存在"),
  ServiceNotFound("0012", "服务不存在"),
  DATA_ACCESS_DENIED("0013", "无数据权限"),
  ;

  public final String code;
  public final String message;

  SystemErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
