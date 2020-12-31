package com.taoyuanx.sso.client;

import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import org.junit.Test;

/**
 * @author dushitaoyuan
 * @date 2020/12/31
 */
public class SSOClientTest {
    @Test
    public void clientTest() {

        SSOClientConfig ssoClientConfig = new SSOClientConfig();
        SSOClient ssoClient = new SSOClientImpl(ssoClientConfig, new HMacVerifySign(ssoClientConfig.getSessionIdSignHmacKey().getBytes()));
        System.out.println(ssoClient.loginCheck("11"));
    }
}
