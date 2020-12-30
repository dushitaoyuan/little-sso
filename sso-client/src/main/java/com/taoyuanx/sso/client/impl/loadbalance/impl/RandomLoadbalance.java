package com.taoyuanx.sso.client.impl.loadbalance.impl;


import com.taoyuanx.sso.client.core.SSOServer;
import com.taoyuanx.sso.client.impl.loadbalance.ILoadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadbalance implements ILoadbalance {
    Random random = new Random();

    @Override
    public SSOServer chose(List<SSOServer> serverList) {
        if (serverList == null || serverList.isEmpty()) {
            return null;
        }
        SSOServer choseServer = null;
        if (serverList.size() == 1) {
            choseServer = serverList.get(0);
        } else {
            choseServer = serverList.get(random.nextInt(serverList.size()));
        }
        return choseServer;
    }
}