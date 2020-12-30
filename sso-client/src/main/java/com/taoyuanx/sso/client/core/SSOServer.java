package com.taoyuanx.sso.client.core;

import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc: server
 */
public class SSOServer {
    private String serverUrl;
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public SSOServer(String serverUrl) {
        this.serverUrl = serverUrl;
    }


    public String getServerUrl() {
        return serverUrl;
    }



    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void alive(boolean alive) {
        if (this.alive != alive) {
            synchronized (this) {
                this.alive = alive;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SSOServer that = (SSOServer) o;
        return Objects.equals(serverUrl, that.serverUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverUrl);
    }
}
