package com.taoyuanx.sso.config;

import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.filter.SSOFilter;
import com.taoyuanx.sso.client.filter.SSOTokenFilter;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import com.taoyuanx.sso.client.impl.SSOTokenClient;
import com.taoyuanx.sso.client.token.AbstractSSOTokenVerify;
import com.taoyuanx.sso.client.token.impl.MySSOTokenVerify;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    public FilterRegistrationBean ssoFilter(SSOClientConfig ssoClientConfig, SSOTokenClient ssoTokenClient) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SSOTokenFilter(ssoClientConfig, ssoTokenClient));
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
    public AbstractSSOTokenVerify ssoTokenVerify(SSOClientConfig clientConfig) {
        return new MySSOTokenVerify(new HMacVerifySign(clientConfig.getClientSessionTokenHmacKey().getBytes()));
    }

    @Bean
    public SSOTokenClient ssoTokenClient(SSOClientConfig clientConfig, AbstractSSOTokenVerify ssoTokenVerify) {
        return new SSOTokenClient(clientConfig, ssoTokenVerify);
    }


}
