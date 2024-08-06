package com.example.parking5.ui.system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.databinding.FragmentBillBinding;
import com.example.parking5.datamodel.CarHistory;
import com.example.parking5.datamodel.LinePay;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.datamodel.ECPayData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class BillFragment extends Fragment {

    private BillViewModel mViewModel;
    private FragmentBillBinding binding;

    public static BillFragment newInstance() {
        return new BillFragment();
    }

    Var<ECPayData> data;
    Var<LinePay> linePayData;
    int printStatus;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(BillViewModel.class);

        binding = FragmentBillBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data = new Var<>();
        linePayData = new Var<>();
        getECPayData();
        getLinePayData();
        setUi();
        setToggleEvent();
        binding.buttonSave.setOnClickListener(v -> {
            updateECPayData();
            updateLinePayData();
        });
        return root;
    }

    private void setUi() {
        if (data.get() != null) {
            //print status toggle buttons
            printStatus = data.get().getPrintStatus();
            switch (printStatus) {
                case 0:
                    binding.toggleButtonPrintBill.setChecked(true);
                    break;
                case 1:
                    binding.toggleButtonPrintReceipt.setChecked(true);
                    break;
                case 2:
                    binding.toggleButtonPrintNone.setChecked(true);
                    break;
                default:
                    break;
            }
            //car number plus switch
            if (data.get().getPlusCarNumber() == 1) {
                binding.switchPrintCarNumber.setChecked(true);
            }
            binding.editTextMachineId.setText(data.get().getMachineID());
            binding.editTextMerchantId.setText(data.get().getMerchantID());
            binding.editTextCompanyId.setText(data.get().getCompanyID());
            binding.editTextHashKey.setText(data.get().getHashKey());
            binding.editTextHashIV.setText(data.get().getHashIV());
            binding.switchInvoiceTestVersion.setChecked(data.get().getTest() == 1);
        }

        if (linePayData.get() != null) {
            binding.editTextLinePayId.setText(linePayData.get().getChannelId());
            binding.editTextLinePaySecret.setText(linePayData.get().getChannelSecret());
            binding.switchLinePayTestVersion.setChecked(linePayData.get().getTest() == 1);
        }
    }

    private void setToggleEvent() {
        ToggleButton buttonPrintBill = binding.toggleButtonPrintBill;
        ToggleButton buttonPrintReceipt = binding.toggleButtonPrintReceipt;
        ToggleButton buttonPrintNone = binding.toggleButtonPrintNone;
        buttonPrintBill.setOnClickListener(v -> {
            boolean isCheck = ((ToggleButton) v).isChecked();
            if (isCheck) {
                buttonPrintReceipt.setChecked(false);
                buttonPrintNone.setChecked(false);
            } else if (printStatus == 0) {
                ((ToggleButton) v).setChecked(true);
            }
            printStatus = 0;
        });
        buttonPrintReceipt.setOnClickListener(v -> {
            boolean isCheck = ((ToggleButton) v).isChecked();
            if (isCheck) {
                buttonPrintBill.setChecked(false);
                buttonPrintNone.setChecked(false);
            } else if (printStatus == 1) {
                ((ToggleButton) v).setChecked(true);
            }
            printStatus = 1;
        });
        buttonPrintNone.setOnClickListener(v -> {
            boolean isCheck = ((ToggleButton) v).isChecked();
            if (isCheck) {
                buttonPrintReceipt.setChecked(false);
                buttonPrintBill.setChecked(false);
            } else if (printStatus == 2) {
                ((ToggleButton) v).setChecked(true);
            }
            printStatus = 2;
        });
    }

    private void getECPayData() {
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getECPay();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < 1; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        data.set(new ECPayData(obj.getInt("print_status"), obj.getInt("plus_car_number"), obj.getString("merchant_id"),
                                obj.getString("company_id"), obj.getString("hash_key"), obj.getString("hash_iv"), obj.getString("machine_id"), obj.getInt("test")));

                    }
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
    }

    private void getLinePayData() {
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getLinePay();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < 1; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        LinePay linePay = gson.fromJson(obj.toString(), LinePay.class);
                        this.linePayData.set(linePay);
                    }
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
    }

    private void updateECPayData() {
        Thread t = new Thread(() -> {
            ApacheServerRequest.updateECPay(printStatus, binding.switchPrintCarNumber.isChecked() ? 1 : 0,
                    binding.editTextMerchantId.getText().toString(),
                    binding.editTextCompanyId.getText().toString(),
                    binding.editTextHashKey.getText().toString(),
                    binding.editTextHashIV.getText().toString(),
                    binding.editTextMachineId.getText().toString(),
                    binding.switchInvoiceTestVersion.isChecked() ? 1 : 0);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLinePayData() {
        Thread t = new Thread(() -> {
            ApacheServerRequest.updateLinePay(
                    binding.editTextLinePayId.getText().toString(),
                    binding.editTextLinePaySecret.getText().toString(),
                    binding.switchLinePayTestVersion.isChecked() ? 1 : 0);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BillViewModel.class);
        // TODO: Use the ViewModel
    }

}