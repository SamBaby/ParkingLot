package com.example.parking5.ui.revenue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.databinding.FragmentCarSettingBinding;
import com.example.parking5.datamodel.BasicFee;
import com.example.parking5.datamodel.CarSlot;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarSettingFragment extends Fragment {
    private FragmentCarSettingBinding binding;
    private CarSettingViewModel mViewModel;
    private TextView carSlotCondition;
    private TextView pregnantSlotCondition;
    private TextView chargingSlotCondition;
    private TextView disabledSlotCondition;
    private TextView reservedSlotCondition;
    private EditText carSlotSetting;
    private EditText pregnantSlotSetting;
    private EditText chargingSlotSetting;
    private EditText disabledSlotSetting;
    private EditText reservedSlotSetting;
    private EditText carSlotLeftSetting;
    private EditText pregnantSlotLeftSetting;
    private EditText chargingSlotLeftSetting;
    private EditText disabledSlotLeftSetting;

    public static CarSettingFragment newInstance() {
        return new CarSettingFragment();
    }

    private Var<CarSlot> carSlot;
    private int carInside;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(CarSettingViewModel.class);

        binding = FragmentCarSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        carSlot = new Var<>();
        carInside = 0;

        carSlotCondition = binding.textViewCarSlotConditionNumber;
        pregnantSlotCondition = binding.textViewPregnantSlotConditionNumber;
        chargingSlotCondition = binding.textViewChargingSlotConditionNumber;
        disabledSlotCondition = binding.textViewDisabledSlotConditionNumber;
        reservedSlotCondition = binding.textViewRegularReservationSlotConditionNumber;

        carSlotSetting = binding.editTextCarSetting;
        pregnantSlotSetting = binding.editTextPregnantSetting;
        disabledSlotSetting = binding.editTextDisabledSetting;
        chargingSlotSetting = binding.editTextChargingSetting;
        reservedSlotSetting = binding.editTextReservedSetting;

        carSlotLeftSetting = binding.editTextCarLeftSetting;
        pregnantSlotLeftSetting = binding.editTextPregnantLeftSetting;
        disabledSlotLeftSetting = binding.editTextDisabledLeftSetting;
        chargingSlotLeftSetting = binding.editTextChargingLeftSetting;

        getCarSlotWithThreadJoin();
        if (carSlot.get() != null) {
            carSlotCondition.setText(String.valueOf(carSlot.get().getCar_slot() - carInside));
            pregnantSlotCondition.setText(String.valueOf(carSlot.get().getPregnant_slot()));
            chargingSlotCondition.setText(String.valueOf(carSlot.get().getDisabled_slot()));
            disabledSlotCondition.setText(String.valueOf(carSlot.get().getCharging_slot()));
            reservedSlotCondition.setText(String.valueOf(carSlot.get().getReserved_slot()));

            carSlotSetting.setText(String.valueOf(carSlot.get().getCar_slot()));
            pregnantSlotSetting.setText(String.valueOf(carSlot.get().getPregnant_slot()));
            disabledSlotSetting.setText(String.valueOf(carSlot.get().getDisabled_slot()));
            chargingSlotSetting.setText(String.valueOf(carSlot.get().getCharging_slot()));
            reservedSlotSetting.setText(String.valueOf(carSlot.get().getReserved_slot()));
        }

        carSlotLeftSetting.setText("0");
        pregnantSlotLeftSetting.setText("0");
        disabledSlotLeftSetting.setText("0");
        chargingSlotLeftSetting.setText("0");

        Button btnRefresh = binding.buttonRefresh;
        btnRefresh.setOnClickListener(v -> {
            Thread t = new Thread(()->{
                getCarSlot();
            });
            try {
                t.start();
                t.join();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(carSlot.get() == null){
                return;
            }
            carSlotCondition.setText(String.valueOf(carSlot.get().getCar_slot() - carInside));
            pregnantSlotCondition.setText(String.valueOf(carSlot.get().getPregnant_slot()));
            chargingSlotCondition.setText(String.valueOf(carSlot.get().getDisabled_slot()));
            disabledSlotCondition.setText(String.valueOf(carSlot.get().getCharging_slot()));
            reservedSlotCondition.setText(String.valueOf(carSlot.get().getReserved_slot()));
        });
        return root;
    }

    private void getCarSlot() {
        try {
            String slot = ApacheServerReqeust.getLeftLot();
            if (slot != null && !slot.isEmpty()) {
                JSONObject obj = new JSONArray(slot).getJSONObject(0);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                carSlot.set(gson.fromJson(obj.toString(), CarSlot.class));
            }

            String cars = ApacheServerReqeust.getCarInsideCount();
            if (cars != null && !cars.isEmpty()) {
                JSONObject obj = new JSONArray(cars).getJSONObject(0);
                carInside = obj.getInt("COUNT(*)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCarSlotWithThread() {
        Thread t = new Thread(this::getCarSlot);
        t.start();
    }

    private void getCarSlotWithThreadJoin() {
        Thread t = new Thread(this::getCarSlot);
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
        mViewModel = new ViewModelProvider(this).get(CarSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}