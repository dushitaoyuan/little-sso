package com.taoyuanx.sso.client.impl.loadbalance;

import com.taoyuanx.sso.client.core.SSOServer;

import java.util.List;

/**
 * @author dushitaoyuan
 * @date 2020/4/512:40
 * 负载接口
 */
public interface ILoadbalance {
    SSOServer chose(List<SSOServer> serverList);
}
