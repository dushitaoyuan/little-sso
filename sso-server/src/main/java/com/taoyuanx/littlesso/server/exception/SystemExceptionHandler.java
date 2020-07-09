package com.taoyuanx.littlesso.server.exception;

import com.alibaba.fastjson.JSON;
import com.ncs.pm.commons.api.Result;
import com.ncs.pm.commons.api.ResultBuilder;
import com.ncs.pm.commons.utils.ResponseUtil;
import com.taoyuanx.littlesso.server.utils.ExceptionHandUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class SystemExceptionHandler implements HandlerExceptionResolver {
    public static final Logger LOG = LoggerFactory.getLogger(SystemExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        InnerError innerError = ExceptionHandUtil.handleException(request, response, e);
        if (ExceptionHandUtil.isJson(request, handler)) {
            Result errorResult = ResultBuilder.failed(innerError.getErrorCode(), innerError.getErrorMsg());
            //处理json请求
            ResponseUtil.responseJson(response, JSON.toJSONString(errorResult), innerError.getHttpCode());
            return modelAndView;
        } else {
            // 处理页面转发请求
            modelAndView.setStatus(HttpStatus.valueOf(innerError.getHttpCode()));
            Map<String, Object> error = new HashMap<>();
            error.put("title", "异常页面，详情联系管理员");
            error.put("errorDesc", innerError);
            modelAndView.addObject("error", error);
            modelAndView.setViewName("error/error");
            LOG.error("请求地址[{}] 出现异常，方法：{}.{}，异常摘要:{}", request.getRequestURI(), e);
            return modelAndView;

        }
    }
}





