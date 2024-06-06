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
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.datamodel.ECPayData;

import org.json.JSONArray;
import org.json.JSONObject;

public class BillFragment extends Fragment {

    private BillViewModel mViewModel;
    private FragmentBillBinding binding;

    public static BillFragment newInstance() {
        return new BillFragment();
    }

    Var<ECPayData> data;
    int printStatus;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(BillViewModel.class);

        binding = FragmentBillBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        data = new Var<>();
        getECPayData();
        setUi();
        setToggleEvent();
        binding.buttonSave.setOnClickListener(v -> {
            updateECPayData();
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
                String json = ApacheServerReqeust.getECPay();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < 1; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        data.set(new ECPayData(obj.getInt("print_status"), obj.getInt("plus_car_number"), obj.getString("merchant_id"),
                                obj.getString("company_id"), obj.getString("hash_key"), obj.getString("hash_iv"), obj.getString("machine_id")));

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
            ApacheServerReqeust.updateECPay(printStatus, binding.switchPrintCarNumber.isChecked() ? 1 : 0,
                    binding.editTextMerchantId.getText().toString(),
                    binding.editTextCompanyId.getText().toString(),
                    binding.editTextHashKey.getText().toString(),
                    binding.editTextHashIV.getText().toString(),
                    binding.editTextMachineId.getText().toString());
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