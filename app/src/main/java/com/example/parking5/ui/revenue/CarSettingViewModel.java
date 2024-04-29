package com.example.parking5.ui.revenue;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parking5.util.HTTPGetRequest;

import org.json.JSONObject;

public class CarSettingViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> carSlotCondition;
    private final MutableLiveData<String> pregnantCondition;
    private final MutableLiveData<String> chargingCondition;
    private final MutableLiveData<String> disabledCondition;
    private final MutableLiveData<String> reservedCondition;

    public CarSettingViewModel() {
        this.carSlotCondition = new MutableLiveData<>("0");
        this.pregnantCondition = new MutableLiveData<>("0");
        this.chargingCondition = new MutableLiveData<>("0");
        this.disabledCondition = new MutableLiveData<>("0");
        this.reservedCondition = new MutableLiveData<>("0");
        getSlotCondition();
    }

    private void getSlotCondition() {
//        new Thread(() -> {
//            try {
//                String slot = HTTPGetRequest.get("func=slot_search");
//                if (slot != null && !slot.isEmpty()) {
//                    JSONObject obj = new JSONObject(slot);
//                    carSlotCondition.postValue(String.valueOf(obj.getInt("car_slot")));
//                    pregnantCondition.postValue(String.valueOf(obj.getInt("pregnant_slot")));
//                    chargingCondition.postValue(String.valueOf(obj.getInt("charging_slot")));
//                    disabledCondition.postValue(String.valueOf(obj.getInt("disabled_slot")));
//                    reservedCondition.postValue(String.valueOf(obj.getInt("reserved_slot")));
//                }
//            } catch (Exception e) {
//                Log.d("getLeftLots", "getLeftLots");
//            }
//        }).start();
    }


    public LiveData<String> getCarSlotCondition() {
        return carSlotCondition;
    }

    public LiveData<String> getPregnantSlotCondition() {
        return pregnantCondition;
    }

    public LiveData<String> getChargingSlotCondition() {
        return chargingCondition;
    }

    public LiveData<String> getDisabledSlotCondition() {
        return disabledCondition;
    }

    public LiveData<String> getReservedSlotCondition() {
        return reservedCondition;
    }
}