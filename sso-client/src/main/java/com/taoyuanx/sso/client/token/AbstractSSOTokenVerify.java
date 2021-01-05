package com.taoyuanx.sso.client.token;


import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.ex.SSOTokenException;

/**
 * @author dushitaoyuan
 * @desc sso token 校验接口
 * @date 2021/1/5
 */
public abstract class AbstractSSOTokenVerify {

    public abstract SSOUser parseToken(String token) throws SSOTokenException;

    public abstract boolean verify(String token, Integer matchTokenType)  throws SSOTokenException;


}
