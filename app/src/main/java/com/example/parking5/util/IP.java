package com.example.parking5.util;


public class IP {
    private static volatile IP instance;
    private String ip;

    public static IP getInstance() {
        if (instance == null) {
            instance = new IP();
        }
        return instance;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }
}
