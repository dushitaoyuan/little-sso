package com.taoyuanx.littlesso.server.utils;

import com.alibaba.fastjson.JSONException;
import com.ncs.pm.commons.api.ResultCode;
import com.ncs.pm.commons.utils.ResponseUtil;
import com.taoyuanx.littlesso.server.exception.InnerError;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.exception.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * @author dushitaoyuan
 * @desc 异常处理工具类
 * @date 2019/12/24
 */
public class ExceptionHandUtil {
    public static final Logger LOG = LoggerFactory.getLogger(ExceptionHandUtil.class);

    public static InnerError handleException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        HttpStatus httpStatus = HttpStatus.OK;
        Integer errorCode = null;
        String errorMsg = e.getMessage();
        if (e instanceof ValidatorException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorCode = ResultCode.PARAM_ERROR.code;
        } else if (e instanceof MethodArgumentNotValidException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorCode = ResultCode.PARAM_ERROR.code;
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            if (bindingResult.hasErrors()) {
                errorMsg = "参数异常:" + bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("\n"));
            }
        } else if (e instanceof BindException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorCode = ResultCode.PARAM_ERROR.code;
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            if (bindingResult.hasErrors()) {
                errorMsg = "参数异常:" + bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("\n"));
            }
        } else if (e instanceof ServiceException) {
            ServiceException exception = (ServiceException) e;
            errorCode = exception.getErrorCode();
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            errorCode = ResultCode.UN_SUPPORT_MEDIATYPE.code;
            HttpMediaTypeNotSupportedException mediaEx = (HttpMediaTypeNotSupportedException) e;
            errorMsg = "不支持该媒体类型:" + mediaEx.getContentType();
        } else if (e instanceof JSONException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorCode = ResultCode.PARAM_ERROR.code;
            errorMsg = "参数异常,json格式非法";
        } else if (e instanceof ServletException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = ResultCode.INTERNAL_SERVER_ERROR.code;
        } else if (e instanceof NoHandlerFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            errorCode = ResultCode.NOT_FOUND.code;
            errorMsg = "请求 [" + ((NoHandlerFoundException) e).getRequestURL() + "] 不存在";
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = ResultCode.INTERNAL_SERVER_ERROR.code;
            errorMsg = "系统内部错误，请联系管理员";
            LOG.error("系统未知异常,异常信息:", e);
        }
        InnerError innerError = new InnerError();
        innerError.setErrorCode(errorCode);
        innerError.setHttpCode(httpStatus.value());
        innerError.setErrorMsg(errorMsg);
        innerError.setException(e);
        return innerError;
    }

    public static boolean isJson(HttpServletRequest request, Object handler) {
        String accept = request.getHeader("Accept");
        if ((accept != null && accept.contains("json"))) {
            return true;
        } else {
            if (ResponseUtil.isAjaxRequest(request)) {
                return true;
            }
            if (handler != null && handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (handlerMethod.hasMethodAnnotation(ResponseBody.class)) {
                    return true;
                }
            }
            return false;
        }


    }

    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            response.sendRedirect(request.getServletContext().getContextPath() + path);
        } catch (Exception e1) {
        }
    }
}
