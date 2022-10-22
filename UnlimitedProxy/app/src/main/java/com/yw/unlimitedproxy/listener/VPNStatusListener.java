package com.yw.unlimitedproxy.listener;

public interface VPNStatusListener {
    void changed(String connectionState, String duration, String lastPacketReceive, String byteIn, String byteOut);
}
