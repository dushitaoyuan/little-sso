package com.ncs.ticket;

import com.alibaba.fastjson.JSON;
import com.ncs.ticket.entity.Ticket;
import com.ncs.ticket.entity.TicketHeader;
import com.ncs.ticket.entity.TicketWrapper;
import com.ncs.ticket.enums.TicketEncodeEnum;
import com.ncs.ticket.error.TicketErrorEnum;
import com.ncs.ticket.exception.TokenException;
import com.ncs.ticket.utils.HelperUtil;
import com.ncs.ticket.utils.RSAUtil;
import org.apache.commons.codec.binary.Base64;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author lianglei
 * @date 2019/1/7 18:30
 * @desc rsa票据
 **/
public class RsaTokenManager {

    //加密票据
    public static String createTicket(Ticket ticket, String signAlgorithm, RSAPublicKey publicKey, RSAPrivateKey privateKey) throws Exception {
        TicketHeader ticketHeader=new TicketHeader();
        ticketHeader.setT(TicketEncodeEnum.RSA.code);
        byte[] header=JSON.toJSONBytes(ticketHeader);
        byte[] data=JSON.toJSONBytes(ticket);
        byte[] encodeData = RSAUtil.encryptByPublicKey(data,publicKey);
        byte[] sign = RSAUtil.sign(data,signAlgorithm,privateKey);
        return TokenForamtUtil.format(header,encodeData,sign);
    }

    //不加密
    public static String createTicket(Ticket ticket, String signAlgorithm, RSAPrivateKey privateKey) throws Exception {
        byte[] data =  JSON.toJSONBytes(ticket);
        byte[] sign = RSAUtil.sign(data,signAlgorithm,privateKey);
        return TokenForamtUtil.format(data,sign);
    }
    public static TicketWrapper vafyTicket(String token, String signAlgorithm, RSAPublicKey publicKey, RSAPrivateKey privateKey) throws Exception {
        try {
            if (HelperUtil.isEmpty(token)) {
                throw new TokenException(TicketErrorEnum.FORMAT_ERROR);
            }
            byte[][] split = TokenForamtUtil.splitTokenToByte(token);
            if (!(split.length==2||split.length==3)) {
                throw new TokenException(TicketErrorEnum.FORMAT_ERROR);
            }
            boolean hasHeader=split.length==3;
            byte[] data=null,sign=null;
            TicketHeader header=null;
            if(hasHeader){
                data=RSAUtil.decryptByPrivateKey(split[TokenForamtUtil.DATA_HEADER_INDEX],privateKey);
                sign=split[TokenForamtUtil.DATA_HEADER_INDEX+1];
                header= JSON.parseObject(split[TokenForamtUtil.HEADER_INDEX], TicketHeader.class);
            }else {
                data = Base64.decodeBase64(split[TokenForamtUtil.DATA_NOHEADER_INDEX]);
                sign=split[TokenForamtUtil.DATA_NOHEADER_INDEX+1];
            }

            Ticket ticket = JSON.parseObject(data, Ticket.class);
            Long end = ticket.getEndTime();
            if (end < System.currentTimeMillis()) {
                throw new TokenException(TicketErrorEnum.EXPIRE);
            }
            if (!RSAUtil.vefySign(data,sign,signAlgorithm,publicKey)) {
                throw new TokenException(TicketErrorEnum.SING_ERROR);
            }
            return new TicketWrapper(ticket,header);
        } catch (TokenException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenException(TicketErrorEnum.UNKNOW_ERROR, e);
        }
    }

}
