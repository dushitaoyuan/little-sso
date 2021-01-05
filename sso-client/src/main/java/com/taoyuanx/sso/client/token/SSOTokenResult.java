package com.taoyuanx.sso.client.token;

import lombok.Data;

@Data
public class SSOTokenResult {
    public static final Integer TOKEN_TYPE_SESSION = 1;
    public static final Integer TOKEN_TYPE_REFRESH = 2;

    private String sessionToken;
    /**
     * 刷新token
     */
    private String refreshToken;
    /**
     * 过期时间
     */
    private Long expire;
}