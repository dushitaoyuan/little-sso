package com.taoyuanx.littlesso.server.login.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ncs.pm.commons.utils.RSAUtil;
import com.ncs.pm.jni.JNIException;
import com.ncs.pm.jni.JniInterfaceImpl;
import com.taoyuanx.littlesso.server.anno.LoginHander;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.commons.SessionConstants;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.enums.LoginTypeEnum;
import com.taoyuanx.littlesso.server.enums.UkeyEncodeTypeEnum;
import com.taoyuanx.littlesso.server.enums.UkeyHashTypeEnum;
import com.taoyuanx.littlesso.server.exception.ValidatorException;
import com.taoyuanx.littlesso.server.log.aop.LoginAuditBuilder;
import com.taoyuanx.littlesso.server.login.handler.ILoginHandler;
import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import com.taoyuanx.littlesso.server.service.LoginService;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.utils.SessionUtil;
import com.taoyuanx.littlesso.server.utils.TicketManager;
import com.taoyuanx.littlesso.server.vo.LoginForm;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import com.taoyuanx.littlesso.server.vo.UKeyLoginForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/7 12:43
 * @desc 钥匙登录实现登录实现
 **/
@LoginHander(support = LoginTypeEnum.UKEY_LOGIN)
@Slf4j
public class UkeyLoginHandler implements ILoginHandler {
    @Autowired
    LoginService loginService;
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SsoServerProperties ssoServerProperties;

    @Override
    public SSOLoginUserVo login(LoginForm loginForm) {

        try {
            JniInterfaceImpl jniApi = JniInterfaceImpl.getInstance();
            UKeyLoginForm uKeyLoginForm = (UKeyLoginForm) loginForm;
            HttpServletRequest request = RequestUtil.getCurrentRequest();
            UkeyEncodeTypeEnum encodeType = UkeyEncodeTypeEnum.type(uKeyLoginForm.getEncodeType());
            //只支持软加密
            if (Objects.isNull(encodeType) || !UkeyEncodeTypeEnum.SOFT.equals(encodeType)) {
                throw new ValidatorException("ukey 登录，加密类型不支持");
            }
            String encodePassword = null;
            try {
                encodePassword = RSAUtil.decryptByPrivateKey(uKeyLoginForm.getEncodePassword(), (RSAPrivateKey) ssoServerProperties.getServerPrivateKey());
            } catch (Exception e) {
                log.warn("解密对称秘钥失败", e);
                throw new ValidatorException("ukey 登录，数据解密失败");
            }
            String decodeData = jniApi.symmDecrypt(encodePassword, uKeyLoginForm.getEncodeData());
            //莫名其妙两遍base64
            JSONObject data = JSON.parseObject(new String(Base64.decodeBase64(Base64.decodeBase64(decodeData))));
            String clientInfo = data.getString("clientInfo");
            UkeyHashTypeEnum type = UkeyHashTypeEnum.type(uKeyLoginForm.getHashType());
            if (Objects.isNull(type)) {
                log.debug("[{}]参数非法,value->[{}]", "hashType", uKeyLoginForm.getHashType());
                throw new ValidatorException("ukey 登录，hash类型不支持");
            }
            String clientInfoHash = jniApi.hashData(type.code, clientInfo);
            String clientInfoSignValue = data.getString("clientInfoSignValue");
            int verifyClientSummarySignResult = jniApi.certVerifySignEX(type.code, clientInfoHash, clientInfoSignValue, uKeyLoginForm.getClientSignCert());
            if (verifyClientSummarySignResult != 0) {
                throw new ValidatorException("ukey 登录，hash签名非法");
            }
            String clientSignCert = uKeyLoginForm.getClientSignCert();
            List<String> certBas64List = ssoServerProperties.getCertBas64List();
            //根证书不为空,验根
            if (CollectionUtil.isNotEmpty(certBas64List)) {
                boolean hasOneMatch = certBas64List.stream().anyMatch((rootCertBase64) -> {
                    try {
                        jniApi.verifyCertValidity(clientSignCert, rootCertBase64);
                        return true;
                    } catch (Exception e) {
                        log.warn("验根异常", e);
                        return false;
                    }
                });
                if (!hasOneMatch) {
                    throw new ValidatorException("ukey 登录，ukey证书非法");
                }

            }
            //验证书是否过期
            if (ssoServerProperties.getUserCertExpireValidate()) {
                int verifyDateResultCode = jniApi.verifyCertIndate(clientSignCert);
                if (verifyDateResultCode != 0 || verifyDateResultCode == -1) {
                    throw new ValidatorException("ukey 登录，ukey证书过期");
                }
            }

            SSOLoginUserVo ssoLoginUserVo = loginService.login(uKeyLoginForm.getClientSignCertSN());
            //登录成功创建会话
            String sessionId = SessionUtil.makeSessionId(String.valueOf(ssoLoginUserVo.getUserId()), "-1");
            ssoLoginUserVo.setSessionId(sessionId);
            String ticket = TicketManager.createTicket(ssoLoginUserVo);
            ssoLoginUserVo.setSessionId(sessionId);
            ssoLoginUserVo.setTicket(ticket);
            SessionStore sessionStore = sessionManager.createSession(sessionId);
            sessionStore.set(AccountConstant.SSO_TICKET_KEY, sessionId);
            sessionStore.set(SessionConstants.USER, ssoLoginUserVo);
            sessionStore.set(SessionConstants.TICKET, ticket);
            sessionStore.set(SessionConstants.REDIRECT_URL, loginForm.getRedirectUrl());
            LoginAuditBuilder.getLoginAudit().setSessionId(sessionId);
            return ssoLoginUserVo;
        } catch (JNIException e) {
            log.error("jni异常", e);
            throw new ValidatorException("服务端类库缺失");
        }
    }


}
