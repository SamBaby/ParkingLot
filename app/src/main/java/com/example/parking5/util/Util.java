package com.example.parking5.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.parking5.R;
import com.example.parking5.event.Var;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
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
}
