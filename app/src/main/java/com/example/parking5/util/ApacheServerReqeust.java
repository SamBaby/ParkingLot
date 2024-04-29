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

    public static String getLeftLot(){
        return HTTPGetRequest.get(url, "func=slot_search");
    }

    public static String getCarInside(){
        return HTTPGetRequest.get(url, "func=cars_inside");
    }
}
