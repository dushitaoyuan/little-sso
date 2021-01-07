package com.taoyuanx.sso.core.session.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taoyuanx.sso.core.dto.SSOTokenUser;
import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.TokenSessionManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 * jwt session 实现
 */
@Slf4j
public class JwtSessionManager implements TokenSessionManager {

    private Long sessionTimeOut;
    private Algorithm algorithm;

    public JwtSessionManager(Algorithm algorithm, Long sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
        this.algorithm = algorithm;
    }


    @Override
    public void createSession(SSOUser ssoUser) {
        createSessionToken(ssoUser);
    }


    @Override
    public boolean isLogin(String sessionId) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .ignoreIssuedAt()
                    .withClaim("type", TokenSessionManager.TOKEN_TYPE_SESSION)
                    .build();
            verifier.verify(sessionId);
            return true;
        } catch (JWTVerificationException e) {
            throw new SessionIdInvalidException(e);
        }
    }

    @Override
    public SSOUser getSSOUser(String sessionId) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .ignoreIssuedAt()
                    .build();
            DecodedJWT decodedJWT = verifier.verify(sessionId);
            return decodeToUser(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new SessionIdInvalidException(e);
        }
    }

    @Override
    public void logout(String sessionId) {
        log.debug("logout nothing to do!");
    }


    private void createSessionToken(SSOUser ssoUser) {
        SSOTokenUser ssoTokenUser = (SSOTokenUser) ssoUser;
        Date now = new Date();
        //过期时间宽松五分钟
        int expireWindow = 5;
        String sessionToken = JWT.create()
                .withNotBefore(now)
                .withExpiresAt(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(sessionTimeOut + expireWindow)))
                .withIssuedAt(now)
                .withSubject(ssoUser.getUsername())
                .withClaim("type", TokenSessionManager.TOKEN_TYPE_SESSION)
                .withClaim("userId", ssoUser.getUserId())
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withNotBefore(now)
                .withExpiresAt(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(sessionTimeOut + expireWindow)))
                .withIssuedAt(now)
                .withSubject(ssoUser.getUsername())
                .withClaim("type", TokenSessionManager.TOKEN_TYPE_REFRESH)
                .withClaim("userId", ssoUser.getUserId())
                .sign(algorithm);
        ssoTokenUser.setSessionId(sessionToken);
        ssoTokenUser.setRefreshToken(refreshToken);
        ssoTokenUser.setExpire(sessionTimeOut * 60);
    }

    private SSOTokenUser decodeToUser(DecodedJWT decode) {
        SSOTokenUser ssoUser = new SSOTokenUser();
        ssoUser.setUsername(decode.getSubject());
        ssoUser.setUserId(decode.getClaim("userId").asLong());
        ssoUser.setTokenType(decode.getClaim("type").asInt());
        return ssoUser;
    }
}
