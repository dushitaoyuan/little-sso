package com.taoyuanx.littlesso.server.utils;

import com.ncs.pm.commons.utils.SpringContextUtil;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import com.ncs.ticket.AuthManager;
import com.ncs.ticket.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/9 16:10
 * @desc 票据创建
 **/
@Slf4j
public class TicketManager {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String createTicket(SSOLoginUserVo ssoLoginUserVo) {
        try {
            SsoServerProperties ssoServerProperties = SpringContextUtil.getBean(SsoServerProperties.class);
            Ticket ticket = new Ticket();
            ticket.setUserID(ssoLoginUserVo.getUserId());
            ticket.setUserAccount(ssoLoginUserVo.getUsername());
            ticket.setClientAddress(ssoLoginUserVo.getClientIp());
            ticket.setUserName(ssoLoginUserVo.getUsername());
            ticket.setRootRs(0);
            ticket.setUserType(ssoLoginUserVo.getUserType());
            ticket.setStrTimeLine(8);
            DateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
            Date now = new Date();
            ticket.setUserAccount(ssoLoginUserVo.getAccountName());
            ticket.setCreateTiime(format.format(now));
            Long end = now.getTime() + ssoServerProperties.getTicketValidTime();
            ticket.setEndTime(format.format(new Date(end)));
            ticket.setLoginTime(ssoLoginUserVo.getLoginDate());
            ticket.setSessionID(ssoLoginUserVo.getSessionId());

            /**
             * 数据zeropading
             */
            if (Objects.nonNull(ssoLoginUserVo.getCenterId())) {
                ticket.setUserPronvinceid(StringFormatUtil.zeroPadding(2, ssoLoginUserVo.getCenterId()));
            }
            if (StringUtils.isNotEmpty(ssoLoginUserVo.getUserSn())) {
                ticket.setUserSn(StringFormatUtil.zeroPadding(10, ssoLoginUserVo.getUserSn()));
            }
            return AuthManager.createTicket(ticket);
        } catch (Exception e) {
            log.debug("票据异常", e);
            throw new ServiceException("票据生成异常");
        }
    }

    /**
     * 解析票据
     */
    public  static  Ticket parseTicket(String ticket) {
        try {
            Ticket ticketObject = AuthManager.parseTicket(ticket);
            return ticketObject;
        } catch (Exception e) {
            log.debug("票据解析异常", e);
            throw new ServiceException("票据生成异常");
        }
    }


}
