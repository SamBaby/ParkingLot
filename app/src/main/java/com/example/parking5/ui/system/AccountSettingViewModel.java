package com.example.parking5.ui.system;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.datamodel.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class AccountSettingViewModel extends ViewModel {
    private final MutableLiveData<Vector<User>> users;

    public AccountSettingViewModel() {
        users = new MutableLiveData<>(new Vector<>());
        refreshUsers();
    }

    public void refreshUsers() {
        Thread t = new Thread(()->{
            try {
                String json = ApacheServerRequest.getUsers();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    users.getValue().clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        users.getValue().add(new User(obj.getString("account"), obj.getString("password"),
                                obj.getString("name"), obj.getString("phone"), obj.getString("permission")));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try{
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MutableLiveData<Vector<User>> getUsers() {
        return users;
    }
}