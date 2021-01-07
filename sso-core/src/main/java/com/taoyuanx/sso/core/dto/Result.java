package com.taoyuanx.sso.core.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 * <p>
 * code 消息码
 * msg 错误消息
 * data 结果体
 * ext 扩展信息
 */
@Data
public class Result implements Serializable {
    private Integer code;
    private String msg;
    private Object data;
    private Object ext;

    public static Result build() {
        return new Result();
    }

    public Result buildCode(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result buildMsg(String msg) {
        this.setMsg(msg);
        return this;
    }


    public Result buildData(Object data) {
        this.data = data;
        return this;

    }

    public Result buildExt(Object ext) {
        this.ext = ext;
        return this;

    }


}
