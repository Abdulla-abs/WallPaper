package com.yw.unlimitedproxy.model;

import java.io.Serializable;
import java.util.List;

public class ServerData implements Serializable {

    private List<ServerBean> all;
    private List<ServerBean> fastServers;

    public List<ServerBean> getAll() {
        return all;
    }

    public void setAll(List<ServerBean> all) {
        this.all = all;
    }

    public List<ServerBean> getFastServers() {
        return fastServers;
    }

    public void setFastServers(List<ServerBean> fastServers) {
        this.fastServers = fastServers;
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "all=" + all +
                ", fastServers=" + fastServers +
                '}';
    }
}
