package com.ncs.ticket;

import com.alibaba.fastjson.JSON;
import com.ncs.ticket.entity.Ticket;
import com.ncs.ticket.error.TicketErrorEnum;
import com.ncs.ticket.exception.TokenException;
import com.ncs.ticket.utils.HelperUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;


/**
 * @author 都市桃源
 * time:2018 下午4:25:55llo
 * usefor: hmac token算法 单向 简单实现
 * 格式: 明文data.sign
 */
public class SimpleTokenManager {

    public static String createTicket(String hmacKey, Ticket ticket) {
        byte[] data =  JSON.toJSONBytes(ticket);
        byte[] sign=HmacUtils.getHmacMd5(hmacKey.getBytes()).doFinal(data);
        return TokenForamtUtil.format(data,sign);
    }

    public static Ticket vafyTicket(String hmacKey, String token) throws Exception {
        try {
            if (HelperUtil.isEmpty(token)) {
                throw new TokenException(TicketErrorEnum.FORMAT_ERROR);
            }
            String[] split =TokenForamtUtil.splitToken(token);
            if (split.length != 2) {
                throw new TokenException(TicketErrorEnum.FORMAT_ERROR);
            }
            byte[] data = Base64.decodeBase64(split[TokenForamtUtil.DATA_NOHEADER_INDEX].getBytes());
            Ticket ticket = JSON.parseObject(data, Ticket.class);
            Long end = ticket.getEndTime();
            if (end < System.currentTimeMillis()) {
                throw new TokenException(TicketErrorEnum.EXPIRE);
            }
            String calcSign = Base64.encodeBase64URLSafeString(HmacUtils.getHmacMd5(hmacKey.getBytes()).doFinal(data));
            if (!calcSign.equals(split[TokenForamtUtil.DATA_NOHEADER_INDEX+1])) {
                throw new TokenException(TicketErrorEnum.SING_ERROR);
            }
            return ticket;
        } catch (TokenException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenException(TicketErrorEnum.UNKNOW_ERROR, e);
        }
    }


}
