package com.taoyuanx.sso.config;


import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.exception.SSOTokenException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.JSONUtil;
import com.taoyuanx.sso.core.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc mvc 配置
 * @date 2020/9/8
 */
@ControllerAdvice
@Slf4j
public class MvcConfig implements WebMvcConfigurer, ResponseBodyAdvice<Object> {


    // 统一结果返回
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        if (containingClass.getClass().getName().startsWith("com.taoyuanx.sso") && AnnotationUtils.findAnnotation(containingClass, RestController.class) != null || AnnotationUtils.findAnnotation(returnType.getMethod(), ResponseBody.class) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends
            HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        }
        if (body instanceof ResponseEntity) {
            return body;
        }
        if (body instanceof String) {
            return body;
        }
        return ResultBuilder.success(body);
    }

    // 统一异常处理
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handle(Exception e, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Integer httpStatus = 500;
        String msg = e.getMessage();
        Integer code = SSOConst.SSO_SERVER_ERROR_CODE;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            httpStatus = responseStatus.code().value();
        }
        if (e instanceof SSOException) {
            httpStatus = 200;
        }else if (e instanceof SessionIdInvalidException ) {
            httpStatus = 200;
            code = SSOConst.LOGIN_CHECK_FAILED_CODE;
        } else if (e instanceof SSOTokenException) {
            httpStatus = 200;
            code = SSOConst.LOGIN_CHECK_FAILED_CODE;
        }else {
            log.error("系统异常", e);
            msg = "系统异常";
        }
        Result failed = ResultBuilder.failed(code, msg);
        response.setStatus(httpStatus);
        if (isResponseJson(request, handlerMethod)) {
            ResponseUtil.responseJson(response, JSONUtil.toJsonString(failed), httpStatus);
            return new ModelAndView();
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("error");
            modelAndView.addObject("errorMsg", msg);
            modelAndView.addObject("errorCode", failed.getCode());
            return modelAndView;
        }
    }

    private boolean isResponseJson(HttpServletRequest request, HandlerMethod handlerMethod) {
        return ResponseUtil.isAcceptJson(request) || (
                Objects.nonNull(handlerMethod) && handlerMethod.hasMethodAnnotation(ResponseBody.class));
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }


}
