package com.example.parking5.datamodel;

public class BasicSetting {
    private String lot_name;
    private String company_name;
    private String company_address;
    private String company_phone;
    private String server_token;
    private String cht_chat_id;
    private String standby_path;
    private int standby_sec;

    private int auto_upload_server;
    private int standby_play;
    private String parking_id;
    private String parking_area;
    private String parking_address;
    private String parking_apikey;

    public BasicSetting(String lotName, String companyName, String companyAddress, String companyPhone, String serverToken, String chtChatId, String standbyPath, int standbySec, int autoUploadServer, int standbyPlay, String parkingId, String parkingArea, String parkingAddress, String parkingApikey) {
        setParking_id(parkingId);
        setParking_area(parkingArea);
        setParking_address(parkingAddress);
        setParking_apikey(parkingApikey);
        setAuto_upload_server(autoUploadServer);
        setStandby_play(standbyPlay);

        setLot_name(lotName);
        setCompany_name(companyName);
        setCompany_address(companyAddress);
        setCompany_phone(companyPhone);
        setServer_token(serverToken);
        setCht_chat_id(chtChatId);
        setStandby_path(standbyPath);
        setStandby_sec(standbySec);
    }

    public String getLot_name() {
        return lot_name;
    }

    public void setLot_name(String lot_name) {
        this.lot_name = lot_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getServer_token() {
        return server_token;
    }

    public void setServer_token(String server_token) {
        this.server_token = server_token;
    }

    public String getCht_chat_id() {
        return cht_chat_id;
    }

    public void setCht_chat_id(String cht_chat_id) {
        this.cht_chat_id = cht_chat_id;
    }

    public String getStandby_path() {
        return standby_path;
    }

    public void setStandby_path(String standby_path) {
        this.standby_path = standby_path;
    }

    public int getStandby_sec() {
        return standby_sec;
    }

    public void setStandby_sec(int standby_sec) {
        this.standby_sec = standby_sec;
    }

    public int getAuto_upload_server() {
        return auto_upload_server;
    }

    public void setAuto_upload_server(int auto_upload_server) {
        this.auto_upload_server = auto_upload_server;
    }

    public int getStandby_play() {
        return standby_play;
    }

    public void setStandby_play(int standby_play) {
        this.standby_play = standby_play;
    }

    public String getParking_id() {
        return parking_id;
    }

    public void setParking_id(String parking_id) {
        this.parking_id = parking_id;
    }

    public String getParking_area() {
        return parking_area;
    }

    public void setParking_area(String parking_area) {
        this.parking_area = parking_area;
    }

    public String getParking_address() {
        return parking_address;
    }

    public void setParking_address(String parking_address) {
        this.parking_address = parking_address;
    }

    public String getParking_apikey() {
        return parking_apikey;
    }

    public void setParking_apikey(String parking_apikey) {
        this.parking_apikey = parking_apikey;
    }
}
