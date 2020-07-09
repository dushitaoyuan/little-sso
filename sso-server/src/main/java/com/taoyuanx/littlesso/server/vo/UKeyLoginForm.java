package com.taoyuanx.littlesso.server.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author lianglei
 * @date 2019/1/7 12:16
 * @desc ukey登录表单
 **/
@Data
public class UKeyLoginForm extends LoginForm {
    /**
     * ukey
     */
    @NotEmpty(message = "ukey识别号不可为空")
    private String clientSignCertSN;

    //client 签名证书
    private String clientSignCert;

    //对称加密密码
    private String encodePassword;
    private String encodeData;

    //非调试环境删除
    private String data;
    //加密类型
    private Integer encodeType;

    private Integer hashType;


}
