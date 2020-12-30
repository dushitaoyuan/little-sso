package com.taoyuanx.sso.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc sso user
 * @date 2020/12/29
 */
@Setter
@Getter
public class SSOUser implements Serializable {

    private String sessionId;

    private Map<String, String> dataInfo;

    private Long createTime;


    private String getData(String dataKey) {
        return dataInfo.get(dataKey);
    }
}
