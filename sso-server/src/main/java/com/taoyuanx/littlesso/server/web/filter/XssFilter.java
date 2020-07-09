package com.taoyuanx.littlesso.server.web.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * xss过滤器
 */
public class XssFilter implements Filter {
    private List<String> excludeUrlList = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeUrls = filterConfig.getInitParameter("excludeUrls");
        if (StringUtils.isNotEmpty(excludeUrls)) {
            excludeUrlList = new ArrayList<>();
            Arrays.stream(excludeUrls.split(",")).forEach(exclude -> {
                excludeUrlList.add(exclude);
            });
        }


    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException {
        if (excludeUrlList != null) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestURI = request.getRequestURI();
            boolean isExclude = excludeUrlList.stream().anyMatch(exclude -> {
                return requestURI.contains(exclude);
            });
            if (!isExclude) {
                //使用包装器
                servletRequest = new XssRequestWrapper(request);
            }

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
