package com.taoyuanx.sso.core.session;

import com.taoyuanx.sso.core.exception.SSOException;

/**
 * @author dushitaoyuan
 * @desc session id generate
 * @date 2020/12/29
 */
public interface SessionIdGenerate {

    String generate(String uniqueData);

    String isSessionIdValid(String sessionId) throws SSOException;

}
