package com.taoyuanx.sso.client.token.impl;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.ex.SSOTokenException;
import com.taoyuanx.sso.client.ex.SessionIdInvalidClientException;
import com.taoyuanx.sso.client.token.AbstractSSOTokenVerify;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 * jwt token校验 实现
 */
@Slf4j
public class JwtSSOTokenVerify extends AbstractSSOTokenVerify {

    private Algorithm algorithm;

    public JwtSSOTokenVerify(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


    @Override
    public SSOUser parseToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .ignoreIssuedAt()
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodeToUser(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new SSOTokenException(e);
        }
    }

    @Override
    public boolean verify(String token, Integer matchTokenType) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .ignoreIssuedAt()
                    .withClaim("type", matchTokenType)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            throw new SessionIdInvalidClientException(e);
        }
    }


    private SSOUser decodeToUser(DecodedJWT decode) {
        SSOUser ssoUser = new SSOUser();
        ssoUser.setSessionId(decode.getToken());
        ssoUser.setUsername(decode.getSubject());
        ssoUser.setUserId(decode.getClaim("userId").asLong());
        return ssoUser;
    }
}
