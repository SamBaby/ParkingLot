package com.example.parking5.datamodel;

public class PreferenceIP {
    private String ip;
    private int vpn;

    public PreferenceIP(String ip, int vpn) {
        this.ip = ip;
        this.vpn = vpn;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getVpn() {
        return vpn;
    }

    public void setVpn(int vpn) {
        this.vpn = vpn;
    }
}
