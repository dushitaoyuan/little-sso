package com.taoyuanx.sso.config;

import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.filter.SSOFilter;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author dushitaoyuan
 * @desc sso 配置
 * @date 2020/12/30
 */
@Configuration
public class SSOConfig {
    @Bean
    public FilterRegistrationBean ssoFilter(SSOClientConfig ssoClientConfig, SSOClient ssoClient) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SSOFilter(ssoClientConfig, ssoClient));
        registration.addUrlPatterns("/*");
        registration.setName("SSOFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public SSOClientConfig ssoClientConfig() {
        return new SSOClientConfig();
    }

    @Bean
    public IVerifySign sessionVerifySign(SSOClientConfig clientConfig) {
        return new HMacVerifySign(clientConfig.getSessionIdSignHmacKey().getBytes());
    }

    @Bean
    public SSOClient ssoClient(SSOClientConfig clientConfig, IVerifySign verifySign) {
        return new SSOClientImpl(clientConfig, verifySign);
    }


}
