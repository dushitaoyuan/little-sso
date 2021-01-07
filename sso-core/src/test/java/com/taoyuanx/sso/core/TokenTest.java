package com.taoyuanx.sso.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.impl.DefaultSignSessionIdGenerate;
import com.taoyuanx.sso.core.session.impl.MyTokenSessionManager;
import com.taoyuanx.sso.core.token.SimpleTokenManager;
import com.taoyuanx.sso.core.token.SuperToken;
import com.taoyuanx.sso.core.token.sign.impl.HMacSign;
import com.taoyuanx.sso.core.token.sign.impl.RsaSign;
import com.taoyuanx.sso.core.utils.AntPathMatcher;
import com.taoyuanx.sso.core.utils.RSAUtil;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author lianglei
 * @date 2019/1/8 10:30
 * @desc 测试
 **/
public class TokenTest {

    @Test
    public void myJwtTest() throws Exception {
        String hmacKey = "123456";
        SimpleTokenManager simpleTokenManager = new SimpleTokenManager(new HMacSign("hmacKey".getBytes()));
        SuperToken sessionToken = new SuperToken();
        sessionToken.setEffectTime(System.currentTimeMillis());
        sessionToken.setType(1);
        String token = simpleTokenManager.createToken(sessionToken);
        System.out.println("token:\t" + token);
        System.out.println("verify:\t" + simpleTokenManager.verify(token));
    }

    @Test
    public void antMatchTest() throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.match("/api/*", "/api/demo"));
        System.out.println(antPathMatcher.match("/api/*", "/api/demo/d"));
        System.out.println(antPathMatcher.match("/api/**", "/api/demo/d"));


    }




    @Test
    public void jwtTest() throws Exception {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .sign(algorithm);
            System.out.println(token);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            DecodedJWT decode = verifier.verify(token);
            System.out.println(decode);
        } catch (
                JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }
}
