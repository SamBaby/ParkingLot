package com.example.parking5.data;

import com.example.parking5.data.model.LoggedInUser;
import com.example.parking5.datamodel.User;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<User> login(String username, String password) {
        Var<User> user = new Var<>();
        try {
            // TODO: handle loggedInUser authentication

            Thread t = new Thread(() -> {
                try {
                    String json = ApacheServerReqeust.getUser(username, password);
                    if (json != null) {
                        JSONArray array = new JSONArray(json);
                        if (array.length() > 0) {
                            JSONObject obj = array.getJSONObject(0);
                            user.set(new User(obj.getString("account"), obj.getString("password"),
                                    obj.getString("name"), obj.getString("phone"), obj.getString("permission")));

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            t.join();
//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe", permission);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(user.get() != null){
            return new Result.Success<>(user.get());
        }else{
            return new Result.Error(new IOException("Error logging in"));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}