package com.ncs.ticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lianglei
 * @date 2019/1/8 11:29
 * @desc 票据包装
 **/
@Data
@AllArgsConstructor
public class TicketWrapper {
    Ticket ticket;
    TicketHeader header;
}
