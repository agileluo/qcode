package io.github.agileluo.qcode.config;

import io.github.agileluo.qcode.vo.ResponseVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * json统一封装
 * @author luoml
 *
 */
@ControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        String path = request.getURI().getPath();
        if(path.startsWith("/v2/api-docs") || path.startsWith("/swagger") || path.startsWith("//swagger") || path.startsWith("/manager/swagger")||path.startsWith("/contract/swagger")) {
        	return body;
        }
		if(!MediaType.APPLICATION_JSON.equals(selectedContentType) || ( body != null &&  body.getClass() == String.class)) {
        	return body;
        }
        if (body instanceof ResponseVo) {
            return body;
        }
        return new ResponseVo(body);
    }
}
