package com.example.parking5.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.HTTPGetRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> lotLeft;
    private final MutableLiveData<String> billLeft;
    private final MutableLiveData<String> revenueDay;
    private final MutableLiveData<String> revenueMonth;

    public HomeViewModel() {
        lotLeft = new MutableLiveData<>();
        billLeft = new MutableLiveData<>();
        revenueDay = new MutableLiveData<>();
        revenueMonth = new MutableLiveData<>();
        getLeftLots();
    }

    private void getLeftLots() {
        new Thread(() -> {
            int total = 0;
            try {
                String slot = ApacheServerReqeust.getLeftLot();
                if (slot != null && !slot.isEmpty()) {
                    JSONObject obj =  new JSONArray(slot).getJSONObject(0);
                    total += obj.getInt("car_slot");
                    total += obj.getInt("pregnant_slot");
                    total += obj.getInt("disabled_slot");
                    total += obj.getInt("charging_slot");
                    total += obj.getInt("reserved_slot");
                }

                String cars = ApacheServerReqeust.getCarInside();
                if (cars != null && !cars.isEmpty()) {
                    JSONObject obj = new JSONArray(cars).getJSONObject(0);
                    total -= obj.getInt("COUNT(*)");
                }
                lotLeft.postValue("剩餘車位:" + String.valueOf(total));
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        }).start();
    }

    public LiveData<String> getLots() {
        return lotLeft;
    }

    public LiveData<String> getBills() {
        return billLeft;
    }

    public LiveData<String> getDailyRevenue() {
        return revenueDay;
    }

    public LiveData<String> getMonthlyRevenue() {
        return revenueMonth;
    }
}