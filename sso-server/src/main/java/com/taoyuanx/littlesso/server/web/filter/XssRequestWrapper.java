package com.taoyuanx.littlesso.server.web.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * xss request包装类
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对数组参数进行特殊字符过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        return filter(super.getParameterValues(name));
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return filter(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String[]> newParameterMap = new HashMap<>(parameterMap.size());
        for (String key : parameterMap.keySet()) {
            String[] value = filter(parameterMap.get(key));
            if (Objects.nonNull(value) && value.length > 0) {
                newParameterMap.put(key, value);
            }
        }
        return newParameterMap;
    }


    private String[] filter(String[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 0) {
            return values;
        }
        String[] newValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = filter(values[i]);
        }
        return newValues;
    }

    private String filter(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        //spring的HtmlUtils进行转义
        return HtmlUtils.htmlEscape(value);
    }


}
