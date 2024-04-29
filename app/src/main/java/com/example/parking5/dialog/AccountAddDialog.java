package com.example.parking5.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.parking5.R;

public class AccountAddDialog extends Dialog{
    public Activity c;
    public Dialog d;
    public Button yes, no;

    public AccountAddDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.account_add);
        yes = (Button) findViewById(R.id.confirm_button);
        no = (Button) findViewById(R.id.cancel_button);
    }
}
