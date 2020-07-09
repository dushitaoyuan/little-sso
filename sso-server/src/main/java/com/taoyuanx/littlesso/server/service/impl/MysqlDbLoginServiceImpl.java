package com.taoyuanx.littlesso.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.commons.LoginErrorCode;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.dao.AccountDao;
import com.taoyuanx.littlesso.server.dao.AuditDao;
import com.taoyuanx.littlesso.server.dao.UserDao;
import com.taoyuanx.littlesso.server.entity.*;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.log.aop.LoginAuditBuilder;
import com.taoyuanx.littlesso.server.service.LoginService;
import com.taoyuanx.littlesso.server.service.cache.IPassowrdLoginErrorCountCache;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import com.vip.vjtools.vjkit.time.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lianglei
 * @date 2019/1/8 13:40
 * @desc mysql数据库登录服务
 **/
@Service
public class MysqlDbLoginServiceImpl implements LoginService {
    @Autowired
    AccountDao accountDao;
    @Autowired
    UserDao userDao;
    @Autowired
    AuditDao auditDao;

    @Autowired(required = false)
    IPassowrdLoginErrorCountCache passowrdLoginErrorCountCache;

    @Autowired
    SsoServerProperties ssoServerProperties;


    @Override
    public SSOLoginUserVo login(String username, String password) {
        AccountEntity account = accountDao.findByAccountNum(username);
        if (Objects.isNull(account)) {
            throw new ServiceException(LoginErrorCode.ACCOUNT_NOT_EXIST, "账户不存在");
        }
        LoginAuditEntity loginAudit = LoginAuditBuilder.getLoginAudit();
        loginAudit.setAccountNum(username);
        loginAudit.setOpDesc("口令登录");
        HttpServletRequest request = RequestUtil.getCurrentRequest();
        if (Objects.nonNull(account)) {
            if (AccountConstant.ACCOUNT_INVALID.equals(account.getStatus())) {
                throw new ServiceException(LoginErrorCode.ACCOUNT_INVALID, "账户冻结或失效");
            }
            if (AccountConstant.ACCOUNT_FROZEN.equals(account.getStatus())) {
                if (Objects.isNull(account.getLockedEndtime())) {
                    throw new ServiceException(LoginErrorCode.ACCOUNT_FROZEN, "账户冻结或失效");
                } else {
                    if (account.getLockedEndtime().after(new Date())) {
                        throw new ServiceException(LoginErrorCode.ACCOUNT_FROZEN, "账户冻结,冻结截止时间为:" + DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT, account.getLockedEndtime()));
                    } else {
                        //冻结已截止，允许登录
                        accountDao.unLockedAccountStatus(account.getId(), AccountConstant.ACCOUNT_NORMAL);
                        account.setStatus(AccountConstant.ACCOUNT_NORMAL);
                    }
                }
            }
            Long accountId = account.getId();
            if (!PasswordUtil.passwordEqual(account.getPassword(), password)) {
                /**
                 * 口令登录失败超限 锁定账户
                 */
                if (ssoServerProperties.isPassordErrorLockEnable()) {
                    Integer passordErrorLockCount = ssoServerProperties.getPassordErrorLockCount();
                    if (passowrdLoginErrorCountCache.getErrorCount(account.getId()) > passordErrorLockCount) {
                        accountDao.lockedAccountStatus(accountId, AccountConstant.ACCOUNT_FROZEN, new Date(System.currentTimeMillis() + ssoServerProperties.getPassordErrorLockTime() * 60 * 1000L));
                        throw new ServiceException(StrUtil.format("口令{}分钟内错误已超过{}次,账户锁定{}分钟",
                                ssoServerProperties.getPassordErrorLockTimeWindowMin(), passordErrorLockCount, ssoServerProperties.getPassordErrorLockTime()));
                    }
                    passowrdLoginErrorCountCache.addErrorCount(accountId);
                }
                throw new ServiceException(LoginErrorCode.ACCOUNT_PASSWORD_NOT_MATCH, "账户密码不匹配");
            }
        }
        UserInfoEntity userInfo = userDao.findByAccountId(account.getId());
        if (Objects.isNull(userInfo)) {
            throw new ServiceException("账户未绑定");
        }

        loginAudit.setUserId(userInfo.getUserId());
        loginAudit.setUserCenterId(userInfo.getCertId());
        loginAudit.setUsername(userInfo.getUsername());
        /**
         * 校验用户已授权的业务是否包含来源业务
         */
        List<AppSysInfoEntity> allAuthedAppSys = userDao.findAllAuthedAppSysByUserId(userInfo.getUserIndex(), userInfo.getCenterId());
        String appSysId = RequestUtil.getValue(RequestUtil.getCurrentRequest(), AccountConstant.APP_SYS_PARAM_KEY_OLD);
        if (Objects.nonNull(appSysId)) {
            Long appSysIdLong = Long.parseLong(appSysId);
            Optional<AppSysInfoEntity> appSysFind = allAuthedAppSys.stream().filter(appSysInfo -> {
                return appSysIdLong.equals(appSysInfo.getAppSysId());
            }).findAny();
            if (!appSysFind.isPresent()) {
                throw new ServiceException("业务未授权给该用户");
            }
        }
        //填充用户信息
        SSOLoginUserVo loginUserVo = new SSOLoginUserVo();
        loginUserVo.setAccountId(account.getId());
        loginUserVo.setUserId(userInfo.getUserId());
        loginUserVo.setUsername(username);
        loginUserVo.setAccountName(account.getAccountNum());
        loginUserVo.setUserType(account.getType());
        loginUserVo.setClientIp(RequestUtil.getClientIp(request));
        loginUserVo.setLoginDate(new Date());
        loginUserVo.setCenterId(userInfo.getCenterId());
        loginUserVo.setAuthedAppSys(allAuthedAppSys);
        PkcCertInfoEntity userCert = userDao.findUserCertByUserIndex(userInfo.getUserIndex());
        String userSn = getHolderSn(userCert);
        loginAudit.setUserSn(userSn);
        loginUserVo.setUserSn(userSn);

        return loginUserVo;
    }

    @Override
    public SSOLoginUserVo login(String certSn) {

        PkcCertInfoEntity userCert = userDao.findUserCertByUserSn(certSn);
        if (Objects.isNull(userCert)) {
            throw new ServiceException(LoginErrorCode.UKEY_CERT_NOTVALID, "证书不存在");
        }

        LoginAuditEntity loginAudit = LoginAuditBuilder.getLoginAudit();
        loginAudit.setOpDesc("ukey钥匙登录");
        UserInfoEntity userInfo = userDao.findUserByCertId(userCert.getPkccertId());

        if (Objects.isNull(userInfo)) {
            throw new ServiceException(LoginErrorCode.UKEY_CERT_NOT_BIND_USER, "证书暂未绑定用户");
        }
        loginAudit.setUserId(userInfo.getUserId());
        loginAudit.setUserCenterId(userInfo.getCertId());
        loginAudit.setUsername(userInfo.getUsername());
        loginAudit.setUserSn(certSn);
        /**
         * 校验用户已授权的业务是否包含来源业务
         */
        List<AppSysInfoEntity> allAuthedAppSys = userDao.findAllAuthedAppSysByUserId(userInfo.getUserIndex(), userInfo.getCenterId());
        String appSysId = RequestUtil.getValue(RequestUtil.getCurrentRequest(), AccountConstant.APP_SYS_PARAM_KEY_OLD);
        if (Objects.nonNull(appSysId)) {
            Long appSysIdLong = Long.parseLong(appSysId);
            Optional<AppSysInfoEntity> appSysFind = allAuthedAppSys.stream().filter(appSysInfo -> {
                return appSysIdLong.equals(appSysInfo.getAppSysId());
            }).findAny();
            if (!appSysFind.isPresent()) {
                throw new ServiceException("业务未授权给该用户");
            }
        }
        HttpServletRequest request = RequestUtil.getCurrentRequest();
        //填充用户信息
        SSOLoginUserVo loginUserVo = new SSOLoginUserVo();
        loginUserVo.setUserId(userInfo.getUserId());
        loginUserVo.setUsername(userInfo.getUsername());
        //todo 逻辑待定 ukey 关不关联acount
        loginUserVo.setAccountName(userInfo.getUsername());
        loginUserVo.setUserType(userInfo.getStatus());
        loginUserVo.setClientIp(RequestUtil.getClientIp(request));
        loginUserVo.setLoginDate(new Date());
        loginUserVo.setCenterId(userInfo.getCenterId());
        String userSn = getHolderSn(userCert);
        loginUserVo.setUserSn(userSn);
        loginUserVo.setAuthedAppSys(allAuthedAppSys);
        return loginUserVo;
    }

    private String getHolderSn(PkcCertInfoEntity userCert) {
        if (Objects.nonNull(userCert)) {
            String userSn = "";
            if (StringUtils.isNotEmpty(userCert.getHolderSN())) {
                userSn = userCert.getHolderSN();
            }
            if (StringUtils.isEmpty(userSn)) {
                userSn = String.valueOf(userCert.getPkccertId());
            }

            return userSn;
        }
        return null;
    }
}
