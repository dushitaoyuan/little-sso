package com.taoyuanx.sso.client.impl.loadbalance.impl;



import com.taoyuanx.sso.client.core.SSOServer;
import com.taoyuanx.sso.client.impl.loadbalance.ILoadbalance;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class RoundLoadbalance implements ILoadbalance {

    private LongAdder position = new LongAdder();

    @Override
    public SSOServer chose(List<SSOServer> serverList) {
        if (serverList == null || serverList.isEmpty()) {
            return null;
        }
        SSOServer choseServer = null;
        int len = serverList.size();
        if (len == 1) {
            choseServer = serverList.get(0);
        } else {
            choseServer = serverList.get(round(len));
        }
        return choseServer;
    }

    private int round(Integer size) {
        Integer index = position.intValue();
        if (index < size) {
            return index;
        } else {
            position.reset();
            position.increment();
            return 0;
        }
    }


}
