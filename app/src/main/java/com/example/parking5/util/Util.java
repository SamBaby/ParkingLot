package com.example.parking5.util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.parking5.R;
import com.example.parking5.datamodel.RegularPass;
import com.example.parking5.event.Var;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
    private static String algorithm = "AES/CBC/PKCS7Padding";

    public String ECPayEncrypt(String data, String key, String IV) {
        String URLEncode = encodeValue(data);
        return encrypt(algorithm, URLEncode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
    }

    public String ECPayDecrypt(String data, String key, String IV) {
        String aesDecrypt = decrypt(algorithm, data, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return decode(aesDecrypt);
    }

    private static String encodeValue(String value) {
        String code = null;
        try {
            code = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    private static String decode(String value) {
        String code = null;
        try {
            code = URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String encrypt(String algorithm, String input, SecretKeySpec key,
                                 IvParameterSpec iv) {

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder()
                        .encodeToString(cipherText);
            } else {
                return android.util.Base64.encodeToString(cipherText, 0);
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static String decrypt(String algorithm, String cipherText, SecretKeySpec key,
                                 IvParameterSpec iv) {

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                plainText = cipher.doFinal(Base64.getDecoder()
                        .decode(cipherText));
            } else {
                plainText = cipher.doFinal(android.util.Base64.decode(cipherText, 0));
            }
            return new String(plainText);
        } catch (Exception e) {
            return null;
        }
    }

    public static String ECPayEncrypt(String data, String algorithm, String key, String IV) {
        String URLEncode = encodeValue(data);
        return encrypt(algorithm, URLEncode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
    }

    public static String ECPayDecrypt(String data, String algorithm, String key, String IV) {
        String aesDecrypt = decrypt(algorithm, data, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return decode(aesDecrypt);
    }

    public static void showDateDialog(Context context, TextView v) {
        final View dialogView = View.inflate(context, R.layout.date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                v.setText(String.format(Locale.TAIWAN, "%04d-%02d-%02d", datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));
                alertDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    public static void showDateTimeDialog(Context context, TextView v) {
        final View dialogView = View.inflate(context, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(view -> {
            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
            timePicker.setIs24HourView(true);
            alertDialog.dismiss();
            v.setText(String.format(Locale.TAIWAN, "%04d-%02d-%02d %02d:%02d:%02d", datePicker.getYear(),
                    datePicker.getMonth() + 1,
                    datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0));
            alertDialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
    public static void showMonthSelectDialog(Context context) {
        final View dialogView = View.inflate(context, R.layout.date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//                v.setText(String.format(Locale.TAIWAN, "%04d-%02d-%02d", datePicker.getYear(),
//                        datePicker.getMonth() + 1,
//                        datePicker.getDayOfMonth()));
                alertDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        // 隱藏日期選項，只顯示月份和年份
        dialogView.findViewById(
                dialogView.getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        dialogView.findViewById(
                dialogView.getResources().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
    public static long getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    public static long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return calendar.getTimeInMillis();
    }

    public static long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTimeInMillis();
    }

    public static boolean isAllowedOut(long time) {
        Date date = new Date();
        return (date.getTime() - time) <= 900;
    }

    public static byte[] getBase64Decode(String base64) {
        return android.util.Base64.decode(base64, 0);
    }

    public static String getBase64Encode(byte[] text) {
        return android.util.Base64.encodeToString(text, 0);
    }

    public static RegularPass getRegularCar(String carNumber) {
        Var<RegularPass> ret = new Var<>();
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getRegularCar(carNumber);
            try {
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    JSONObject obj = array.getJSONObject(0);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    RegularPass pass = gson.fromJson(obj.toString(), RegularPass.class);
                    ret.set(pass);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }
}
