package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentCarSettingBinding;
import com.example.parking5.databinding.FragmentPrintSettingBinding;
import com.example.parking5.datamodel.CarSlot;
import com.example.parking5.datamodel.PrintSetting;
import com.example.parking5.util.ApacheServerReqeust;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PrintSettingFragment extends Fragment {
    private FragmentPrintSettingBinding binding;

    private PrintSettingViewModel mViewModel;
    private EditText txtTotal;
    private EditText txtAlert;
    private EditText txtInvoice;
    private EditText txtRevenue;
    private EditText txtCoupon;
    private TextView txtPayLeft;
    private TextView txtOutLeft;
    private PrintSetting printSetting;
    private Button btnModify;
    private Button btnRefresh;
    public static PrintSettingFragment newInstance() {
        return new PrintSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPrintSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        txtTotal = binding.editTextBillTotal;
        txtAlert = binding.editTextPrintAlert;
        txtInvoice = binding.editTextPrintBillMinus;
        txtRevenue = binding.editTextPrintRevenueMinus;
        txtCoupon = binding.editTextPrintCouponMinus;
        txtPayLeft = binding.textViewPayMachinePieceLeft;
        txtOutLeft = binding.textViewOutPieceLeft;

        btnModify = binding.buttonModify;
        btnRefresh= binding.buttonRefresh;

        btnModify.setOnClickListener(v->updateDatabase());
        btnRefresh.setOnClickListener(v->setText());

        setText();
        return root;
    }

    private void updateDatabase() {
        Thread t = new Thread(() -> {
            try {
                String total = txtTotal.getText().toString();
                String warning = txtAlert.getText().toString();
                String invoice = txtInvoice.getText().toString();
                String revenue = txtRevenue.getText().toString();
                String coupon = txtCoupon.getText().toString();
                ApacheServerReqeust.updatePrintSettings(total, warning, invoice, revenue, coupon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setText() {
        getPrintSetting();
        if (printSetting != null) {
            txtTotal.setText(String.valueOf(printSetting.getNew_roll()));
            txtAlert.setText(String.valueOf(printSetting.getWarning()));
            txtInvoice.setText(String.valueOf(printSetting.getPrint_invoice()));
            txtRevenue.setText(String.valueOf(printSetting.getPrint_revenue()));
            txtCoupon.setText(String.valueOf(printSetting.getPrint_coupon()));
            txtPayLeft.setText(String.valueOf(printSetting.getPay_left()));
            txtOutLeft.setText(String.valueOf(printSetting.getExit_left()));
        }
    }

    private void getPrintSetting() {
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerReqeust.getPrintSettings();
                if (res != null && !res.isEmpty()) {
                    JSONObject obj = new JSONArray(res).getJSONObject(0);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    printSetting = gson.fromJson(obj.toString(), PrintSetting.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PrintSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}