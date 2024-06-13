package com.example.parking5.datamodel;

public class CarSlot {
    private int car_slot;
    private int pregnant_slot;
    private int disabled_slot;
    private int charging_slot;
    private int reserved_slot;
    private int car_left;
    private int pregnant_left;
    private int disabled_left;
    private  int charging_left;

    public CarSlot(int carSlot, int pregnantSlot, int disabledSlot, int chargingSlot, int reservedSlot, int carLeft, int pregnantLeft, int disabledLeft, int chargingLeft) {
        setCar_left(carLeft);
        setPregnant_left(pregnantLeft);
        setDisabled_left(disabledLeft);
        setCharging_left(chargingLeft);
        setCar_slot(carSlot);
        setPregnant_slot(pregnantSlot);
        setDisabled_slot(disabledSlot);
        setCharging_slot(chargingSlot);
        setReserved_slot(reservedSlot);
    }

    public int getCar_slot() {
        return car_slot;
    }

    public void setCar_slot(int car_slot) {
        this.car_slot = car_slot;
    }

    public int getPregnant_slot() {
        return pregnant_slot;
    }

    public void setPregnant_slot(int pregnant_slot) {
        this.pregnant_slot = pregnant_slot;
    }

    public int getDisabled_slot() {
        return disabled_slot;
    }

    public void setDisabled_slot(int disabled_slot) {
        this.disabled_slot = disabled_slot;
    }

    public int getCharging_slot() {
        return charging_slot;
    }

    public void setCharging_slot(int charging_slot) {
        this.charging_slot = charging_slot;
    }

    public int getReserved_slot() {
        return reserved_slot;
    }

    public void setReserved_slot(int reserved_slot) {
        this.reserved_slot = reserved_slot;
    }

    public int getCar_left() {
        return car_left;
    }

    public void setCar_left(int car_left) {
        this.car_left = car_left;
    }

    public int getPregnant_left() {
        return pregnant_left;
    }

    public void setPregnant_left(int pregnant_left) {
        this.pregnant_left = pregnant_left;
    }

    public int getDisabled_left() {
        return disabled_left;
    }

    public void setDisabled_left(int disabled_left) {
        this.disabled_left = disabled_left;
    }

    public int getCharging_left() {
        return charging_left;
    }

    public void setCharging_left(int charging_left) {
        this.charging_left = charging_left;
    }
}
