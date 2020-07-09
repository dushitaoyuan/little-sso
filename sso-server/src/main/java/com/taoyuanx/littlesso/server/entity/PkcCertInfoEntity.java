package com.taoyuanx.littlesso.server.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PkcCertInfoEntity implements Serializable {

    private Long pkccertIndex;

    /**
     * 数字证书ID
     */

    private Long pkccertId;

    /**
     * 持有者名
     */
    private String holderName;

    /**
     * 持有者SN
     */
    private String holderSN;

    private String issuer;

    /**
     * 主题
     */
    private String subject;

    /**
     * 有效起始时间
     */
    private Date startTime;

    /**
     * 有效结束时间
     */
    private Date endTime;

    /**
     * 得到证书时间
     */
    private Date getTime;

    /**
     * 说明
     */
    private String pkcNote;

    /**
     * 预留
     */
    private String reserved;

    /**
     * 钥匙类型
     */
    private Integer keyType;

    /**
     * 证书内容
     */
    private byte[] pkcContent;
}