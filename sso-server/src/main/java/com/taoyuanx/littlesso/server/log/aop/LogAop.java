package com.taoyuanx.littlesso.server.log.aop;

import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.dao.AuditDao;
import com.taoyuanx.littlesso.server.entity.LoginAuditEntity;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/11/4 18:11
 * @desc 日志aop
 **/
@Component
@Aspect
@Slf4j
public class LogAop {
    @Autowired
    AuditDao auditDao;


    @Pointcut("execution(* com.ncs.sso.server.web.controller.LoginController.*(..))")
    public void audit() {
    }

    @Around("audit()")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        LoginAuditEntity loginAudit = LoginAuditBuilder.getLoginAudit();
        try {
            if (StringUtils.isEmpty(loginAudit.getOpDesc())) {
                return point.proceed();
            }
            loginAudit.setOpResult(LoginAuditEntity.OP_RESULT_SUCCESS);
            Object result = point.proceed();
            return result;
        } catch (Throwable throwable) {
            loginAudit.setOpResult(LoginAuditEntity.OP_RESULT_FAILED);
            if (throwable instanceof ServiceException) {
                loginAudit.setOpDesc(loginAudit.getOpDesc() + ",失败原因:" + throwable.getMessage());
            }
            throw throwable;
        } finally {
            LoginAuditBuilder.removeLogin();
            if (StringUtils.isNotEmpty(loginAudit.getOpDesc())) {
                HttpServletRequest currentRequest = RequestUtil.getCurrentRequest();
                String clientIp = RequestUtil.getClientIp(currentRequest);
                Object attribute = currentRequest.getSession().getAttribute(AccountConstant.APP_SYS_PARAM_KEY_OLD);
                if (Objects.nonNull(attribute)) {
                    loginAudit.setAppSysId(attribute.toString());
                }
                loginAudit.setOpAddress(clientIp);
                loginAudit.setOpDate(new Date());
                auditDao.insertLoginAudit(loginAudit);
            }


        }
    }


}
