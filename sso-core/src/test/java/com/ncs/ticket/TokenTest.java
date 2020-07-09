package com.ncs.ticket;

import com.ncs.ticket.entity.Ticket;
import com.ncs.ticket.utils.RSAUtil;
import org.junit.Test;

import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * @author lianglei
 * @date 2019/1/8 10:30
 * @desc 测试
 **/
public class TokenTest {

    public Ticket newTicket(){
        Ticket ticket=new Ticket();
        ticket.setUserId(1L);
        ticket.setUserName("name");
        ticket.setCreateTime(new Date().getTime());
        Long valid=4*60*1000L;
        ticket.setEndTime(System.currentTimeMillis()+valid);
        return ticket;
    }


    @Test
    public void rsaTokenTest() throws Exception {
        String password="6PQyb5mSPKQDIeor";
        KeyStore keyStore = RSAUtil.getKeyStore(TokenTest.class.getClassLoader().getResourceAsStream("ticket-rsa.p12"), password);
        RSAPublicKey publicKey = RSAUtil.getPublicKey(keyStore);
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(keyStore, password);
        String ticket = RsaTokenManager.createTicket(newTicket(),null,publicKey,privateKey);
        System.out.println("ticket:\t"+ticket);
        System.out.println("verify:\t"+RsaTokenManager.vafyTicket(ticket,null,publicKey,privateKey));
    }
    @Test
    public void simpleTokenTest() throws Exception {
        String hmacKey="123456";
        String ticket = SimpleTokenManager.createTicket(hmacKey, newTicket());
        System.out.println("ticket:\t"+ticket);
        System.out.println("verify:\t"+SimpleTokenManager.vafyTicket(hmacKey,ticket));
    }
}
