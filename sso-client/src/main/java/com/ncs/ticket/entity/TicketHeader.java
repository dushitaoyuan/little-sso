package com.ncs.ticket.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lianglei
 * @date 2019/1/8 10:47
 * @desc 票据头部
 **/
@Data
public class TicketHeader implements Serializable {
    //票据类型
    private Integer t;
    //加密类型
    private Integer e;
}
