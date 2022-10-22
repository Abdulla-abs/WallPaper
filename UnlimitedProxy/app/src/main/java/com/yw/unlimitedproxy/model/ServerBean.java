package com.yw.unlimitedproxy.model;

import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.utils.FileUtils;

import java.io.Serializable;
import java.util.List;

public class ServerBean implements Serializable{

    private String country;
    private String flag_url;
    private List<Server> server;

    public ServerBean(String country, String flag_url, List<Server> server) {
        this.country = country;
        this.flag_url = flag_url;
        this.server = server;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setFlag_url(String flag_url) {
        this.flag_url = flag_url;
    }
    public String getFlag_url() {
        return flag_url;
    }

    public void setServer(List<Server> server) {
        this.server = server;
    }
    public List<Server> getServer() {
        return server;
    }

    public static class Server implements Serializable {

        private String server_address;
        private String server_key;
        private int server_weight;

        public Server(String server_address, String server_key, int server_weight) {
            this.server_address = server_address;
            this.server_key = server_key;
            this.server_weight = server_weight;
        }

        public void setServer_address(String server_address) {
            this.server_address = server_address;
        }
        public String getServer_address() {
            return server_address;
        }

        public void setServer_key(String server_key) {
            this.server_key = server_key;
        }
        public String getServer_key() {
            return server_key;
        }

        public void setServer_weight(int server_weight) {
            this.server_weight = server_weight;
        }
        public int getServer_weight() {
            return server_weight;
        }

        public boolean isAvailable(){
            return FileUtils.checkOvpnFileExists(this);
        }

        public String getOvpnFileName(){
            String[] split = server_address.split("/");
            return split[split.length-1];
        }

        @Override
        public String toString() {
            return "Server{" +
                    "server_address='" + server_address + '\'' +
                    ", server_key='" + server_key + '\'' +
                    ", server_weight=" + server_weight +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ServerBean{" +
                "country='" + country + '\'' +
                ", flag_url='" + flag_url + '\'' +
                ", server=" + server +
                '}';
    }
}