package io.github.agileluo.qcode.config;

import io.github.agileluo.qcode.util.BizException;
import io.github.agileluo.qcode.util.SystemErrorCode;
import io.github.agileluo.qcode.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@Value("${spring.application.id:00}")
	String appId;
	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String SystemErrorPrefix = "0";
	private static final String BizErrorPrefix = "1";

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public ResponseVo handleException(Exception ex) {
		logger.error("Exception", ex);
		if (ex instanceof BizException) {
			BizException be = (BizException) ex;
			return buildREsponseVo(BizErrorPrefix, be.getMessage(),be.getCode());
		}
		return  buildREsponseVo(SystemErrorPrefix, SystemErrorCode.SystemError.message,  SystemErrorCode.SystemError.code);
	}

	public ResponseVo buildREsponseVo(String prefix, String msg, String code){
		if(code.length() < 7){
			return new ResponseVo(msg, prefix + appId + code);
		}
		return new ResponseVo(msg, code);
	}


	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ResponseBody
	@ExceptionHandler(Throwable.class)
	public ResponseVo handleException(Throwable ex) {
		logger.error("Throwable", ex);
		return buildREsponseVo(SystemErrorPrefix, SystemErrorCode.SystemError.message,  SystemErrorCode.SystemError.code);
	}
	
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseVo validationError(MethodArgumentNotValidException ex) {
		logger.error("Exception", ex);
		Map<String, String> errorMsg = new HashMap<>();
		BindingResult result = ex.getBindingResult();
		if (result.hasErrors()) {
			for (FieldError fieldError : result.getFieldErrors()) {
				errorMsg.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
		}
		
		ResponseVo vo = buildREsponseVo(SystemErrorPrefix, SystemErrorCode.RequestParamIllegal.message, SystemErrorCode.RequestParamIllegal.code);
		vo.setContent(errorMsg);
		return vo;
	}
    
}
