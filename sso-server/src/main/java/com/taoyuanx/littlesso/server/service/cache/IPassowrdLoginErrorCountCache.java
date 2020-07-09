package com.taoyuanx.littlesso.server.service.cache;

/**
 * @author lianglei
 * @date 2020/3/9 14:48
 * @desc 登录计数cache 集群部署时开启redis
 **/
public interface IPassowrdLoginErrorCountCache {

    Integer getErrorCount(Long accountId);

    void addErrorCount(Long accountId);
}
