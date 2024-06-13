package com.example.parking5.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.HTTPGetRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        setLeftLots();
        setBillNumber();
        setRevenueDay();
        setRevenueMonth();
    }

    private void setLeftLots() {
        new Thread(() -> {
            int total = 0;
            try {
                String slot = ApacheServerReqeust.getLeftLot();
                if (slot != null && !slot.isEmpty()) {
                    JSONObject obj = new JSONArray(slot).getJSONObject(0);
                    total += obj.getInt("car_slot");
                    total += obj.getInt("pregnant_slot");
                    total += obj.getInt("disabled_slot");
                    total += obj.getInt("charging_slot");
                    total += obj.getInt("reserved_slot");
                }

                String cars = ApacheServerReqeust.getCarInsideCount();
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

    private void setBillNumber() {
//        new Thread(() -> {
//            int total = 0;
//            try {
//                String slot = ApacheServerReqeust.getLeftLot();
//                if (slot != null && !slot.isEmpty()) {
//                    JSONObject obj = new JSONArray(slot).getJSONObject(0);
//                    total += obj.getInt("car_slot");
//                    total += obj.getInt("pregnant_slot");
//                    total += obj.getInt("disabled_slot");
//                    total += obj.getInt("charging_slot");
//                    total += obj.getInt("reserved_slot");
//                }
//
//                String cars = ApacheServerReqeust.getCarInsideCount();
//                if (cars != null && !cars.isEmpty()) {
//                    JSONObject obj = new JSONArray(cars).getJSONObject(0);
//                    total -= obj.getInt("COUNT(*)");
//                }
//                lotLeft.postValue("剩餘車位:" + String.valueOf(total));
//            } catch (Exception e) {
//                Log.d("getLeftLots", "getLeftLots");
//            }
//        }).start();
        billLeft.postValue("發票剩餘: 0");
    }

    private void setRevenueDay() {
        new Thread(() -> {
            int total = 0;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date start = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date end = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            String startDate = formatter.format(start);
            String endDate = formatter.format(end);
            try {
                String res = ApacheServerReqeust.getPayHistoryWithDates(startDate, endDate, "", "");
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject pay = array.getJSONObject(0);
                        if (pay.has("cost")) {
                            total += pay.getInt("cost");
                        }
                    }
                }
                revenueDay.postValue("當日營收:" + total);
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        }).start();
    }

    private void setRevenueMonth() {
        new Thread(() -> {
            int total = 0;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date start = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date end = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            String startDate = formatter.format(start);
            String endDate = formatter.format(end);
            try {
                String res = ApacheServerReqeust.getPayHistoryWithDates(startDate, endDate, "", "");
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject pay = array.getJSONObject(0);
                        if (pay.has("cost")) {
                            total += pay.getInt("cost");
                        }
                    }
                }

                revenueMonth.postValue("當月營收:" + total);
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