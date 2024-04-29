package com.example.parking5.util;

public class ApacheServerReqeust {
    public static final String url = "192.168.50.5:8080";

    public static String getUsers() {
        return HTTPGetRequest.get(url, "func=user_search");
    }

    public static String getUser(String account, String password) {
        return HTTPGetRequest.get(url, String.format("func=user_single_search&account=%s&password=%s", account, password));
    }

    public static String deleteUser(String account) {
        return HTTPGetRequest.get(url, String.format("func=user_delete&account=%s", account));
    }

    public static String updateUser(String account, String password, String name, String phone, String permission) {
        return HTTPGetRequest.get(url, String.format("func=user_update&account=%s&password=%s&name=%s&phone=%s&permission=%s", account, password, name, phone, permission));
    }

    public static String addUser(String account, String password, String name, String phone, String permission) {
        return HTTPGetRequest.get(url, String.format("func=user_add&account=%s&password=%s&name=%s&phone=%s&permission=%s", account, password, name, phone, permission));
    }

    public static String getLeftLot() {
        return HTTPGetRequest.get(url, "func=slot_search");
    }

    public static String getCarInside() {
        return HTTPGetRequest.get(url, "func=cars_inside");
    }

    public static String getCams() {
        return HTTPGetRequest.get(url, "func=cam_search");
    }

    public static String getCam(String ip) {
        return HTTPGetRequest.get(url, String.format("func=cam_single_search&ip=%s", ip));
    }

    public static String updateCam(int number, String name, int inOut, int pay, int open, String oldIp, String newIp) {
        return HTTPGetRequest.get(url, String.format("func=cam_update&number=%d&name=%s&in_out=%d&pay=%d&open=%d&old_ip=%s&new_ip=%s", number, name, inOut, pay, open, oldIp, newIp));
    }

    public static String addCam(int number, String name, String ip, int inOut, int pay, int open) {
        return HTTPGetRequest.get(url, String.format("func=cam_add&number=%d&name=%s&ip=%s&in_out=%d&pay=%d&open=%d", number, name, ip, inOut, pay, open));
    }

    public static String deleteCam(String ip) {
        return HTTPGetRequest.get(url, String.format("func=cam_delete&ip=%s", ip));
    }

    public static String getECPay() {
        return HTTPGetRequest.get(url, "func=ecpay_search");
    }

    public static String updateECPay(int printStatus, int plusCarNumber, String merchantID, String CompanyID, String key, String IV) {
        return HTTPGetRequest.get(url, String.format("func=ecpay_update&print_status=%d&plus_car_number=%d&merchant_id=%s&company_id=%s&hash_key=%s&hash_iv=%s", printStatus, plusCarNumber, merchantID, CompanyID, key, IV));
    }

    public static String getHolidays() {
        return HTTPGetRequest.get(url, "func=holiday_search");
    }

    public static String getHoliday(String date) {
        return HTTPGetRequest.get(url, String.format("func=holiday_single_search&date=%s", date));
    }

    public static String updateHoliday(int number, String oldDate, String newDate, int weekday, String description, String updateDate, String account) {
        return HTTPGetRequest.get(url, String.format("func=holiday_update&number=%d&old_date=%s&new_date=%s&weekday=%d&description=%s&update_date=%s&account=%s", number, oldDate, newDate, weekday, description, updateDate, account));
    }

    public static String addHoliday(int number, String date, int weekday, String description, String updateDate, String account) {
        return HTTPGetRequest.get(url, String.format("func=holiday_add&number=%d&date=%s&weekday=%d&description=%s&update_date=%s&account=%s", number, date, weekday, description, updateDate, account));
    }

    public static String deleteHoliday(String date) {
        return HTTPGetRequest.get(url, String.format("func=holiday_delete&date=%s", date));
    }
}
