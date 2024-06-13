package com.example.parking5.datamodel;

public class CouponHistory {
    private int id;
    private int time_fee;
    private int amount;
    private int count;
    private String deadline;
    private String user;
    private String mark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime_fee() {
        return time_fee;
    }

    public void setTime_fee(int time_fee) {
        this.time_fee = time_fee;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
