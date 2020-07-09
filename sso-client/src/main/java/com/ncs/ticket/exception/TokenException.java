package com.ncs.ticket.exception;

import com.ncs.ticket.error.TicketErrorEnum;

/**
 * @author dushitaoyuan
 * @desc token token过期异常
 * @date 2019/7/3
 */
public class TokenException extends  Exception {
    public TokenException(TicketErrorEnum ticketErrorEnum, String message) {
        super(message);
    }
    public TokenException(TicketErrorEnum ticketErrorEnum) {
        super(ticketErrorEnum.desc);
    }
    public TokenException(TicketErrorEnum ticketErrorEnum,String message, Throwable cause) {
        super(message, cause);
    }
    public TokenException(TicketErrorEnum ticketErrorEnum, Throwable cause) {
        super(ticketErrorEnum.desc, cause);
    }
}
