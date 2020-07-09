package com.taoyuanx.littlesso.server.web.controller;

import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.commons.SessionConstants;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import com.taoyuanx.littlesso.server.utils.RandomCodeUtil;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.utils.SessionUtil;
import com.taoyuanx.littlesso.server.utils.TicketManager;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import com.ncs.ticket.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/6 18:24
 * @desc 页面控制器
 **/
@Controller
@Slf4j
public class PageController {
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SsoServerProperties ssoServerProperties;
    Long needReLoginTime = 30 * 60 * 1000L;

    /**
     * 登录页
     */
    @GetMapping({"", "index", "login"})
    public String index(Model model, HttpServletRequest request) throws Exception {
        String redirectUrl = RequestUtil.getRedirectUrl(request);
        String sso_s = RequestUtil.getValue(request, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY);
        if (Objects.isNull(redirectUrl)) {
            redirectUrl = "";
        }
        //session 已存在,并且未过期,重定向到成功页面,过期重新登录
        String sessionId = RequestUtil.getValue(request, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY);
        if (StringUtils.isNotEmpty(sessionId) && SessionUtil.isValidSessionId(sessionId) && StringUtils.isEmpty(sso_s)) {
            SessionStore sessionStore = sessionManager.getSessionStore(sessionId);
            if (Objects.isNull(sessionStore)) {
                /**
                 * session 不存在,验证票据
                 */
                String base64Ticket = RequestUtil.getValue(request, AccountConstant.SSO_TICKET_KEY);
                if (StringUtils.isNotEmpty(base64Ticket)) {
                    /*if (ssoServerProperties.getSsoServerMode().equals("redirect") && StringUtils.isNotEmpty(redirectUrl)) {
                        try {
                            Ticket ticket = TicketManager.parseTicket(base64Ticket);
                            DateFormat format = new SimpleDateFormat(TicketManager.DATETIME_FORMAT);
                            Date endTime = format.parse(ticket.getEndTime());
                            if (endTime.getTime() - System.currentTimeMillis() > needReLoginTime) {
                                return "redirect:success?" + AccountConstant.SSO_GLOBAL_SESSION_ID_KEY + "=" + sessionId + "&" + AccountConstant.REDIRECT_URL_PARAM_KEY + "=" + redirectUrl;
                            }
                        } catch (Exception e) {
                            log.warn("票据解析异常", e);
                        }
                    }*/
                    try {
                        Ticket ticket = TicketManager.parseTicket(base64Ticket);
                        DateFormat format = new SimpleDateFormat(TicketManager.DATETIME_FORMAT);
                        Date endTime = format.parse(ticket.getEndTime());
                        if (endTime.getTime() - System.currentTimeMillis() > needReLoginTime) {
                            return "redirect:success?" + AccountConstant.SSO_GLOBAL_SESSION_ID_KEY + "=" + sessionId + "&" + AccountConstant.REDIRECT_URL_PARAM_KEY + "=" + redirectUrl;
                        }
                    } catch (Exception e) {
                        log.warn("票据解析异常", e);
                    }

                }
            } else if (System.currentTimeMillis() + needReLoginTime < sessionStore.getEndTime()) {
                return "redirect:success?" + AccountConstant.SSO_GLOBAL_SESSION_ID_KEY + "=" + sessionId + "&" + AccountConstant.REDIRECT_URL_PARAM_KEY + "=" + redirectUrl;
            }
        }
        String random = RandomCodeUtil.getRandCode(15);
        //服务端随机数
        model.addAttribute(AccountConstant.LOGIN_RANDOM, random);
        request.getSession().setAttribute(AccountConstant.LOGIN_RANDOM, random);
        //服务端公钥
        model.addAttribute("serverPub", Base64.encodeBase64String(ssoServerProperties.getServerPublicKey().getEncoded()));
        //跳转地址
        model.addAttribute("redirectUrl", redirectUrl);
        //设置业务标识
        request.getSession().setAttribute(AccountConstant.APP_SYS_PARAM_KEY_OLD, request.getParameter(AccountConstant.APP_SYS_PARAM_KEY_OLD));
        //设置跳转url
        request.getSession().setAttribute(AccountConstant.REDIRECT_URL_PARAM_KEY, redirectUrl);

        return "login";
    }

    /**
     * 登录成功页面
     */
    @RequestMapping({"success"})
    public String success(HttpServletRequest request, Model model) {
        if (ssoServerProperties.getSsoServerMode().equalsIgnoreCase("localSite")) {
            Object redirectUrl = request.getSession().getAttribute(AccountConstant.REDIRECT_URL_PARAM_KEY);
            //如果携带有跳转地址,直接跳转
            if (Objects.nonNull(redirectUrl) && StringUtils.isNotEmpty(redirectUrl.toString())) {
                return "forward:appSysPage";
            }
            //业务列表
            String sessionId = RequestUtil.getValue(request, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY);
            if (StringUtils.isEmpty(sessionId) || !SessionUtil.isValidSessionId(sessionId)) {
                throw new ServiceException("操作非法,会话无效");
            }


            String appSysListPage = "success/success_syslist";
            SessionStore sessionStore = sessionManager.getSessionStore(sessionId);
            SSOLoginUserVo loginUserVo = sessionStore.get(SessionConstants.USER, SSOLoginUserVo.class);
            model.addAttribute("ticket", sessionStore.get(SessionConstants.TICKET));
            model.addAttribute("user", loginUserVo);
            model.addAttribute("sessionId", loginUserVo.getSessionId());

            model.addAttribute("userAppSysList", loginUserVo.getAuthedAppSys());
            return appSysListPage;
        } else if (ssoServerProperties.getSsoServerMode().equalsIgnoreCase("redirect")) {
            //跳转到具体业务系统
            return "forward:appSysPage";
        }
        throw new ServiceException("登录成功,转发异常");

    }



}
