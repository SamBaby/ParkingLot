package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentCameraSettingBinding;
import com.example.parking5.databinding.FragmentCarSettingBinding;
import com.example.parking5.databinding.FragmentHomeBinding;
import com.example.parking5.ui.home.HomeViewModel;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(CarSettingViewModel.class);

        binding = FragmentCarSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        carSlotCondition = binding.textViewCarSlotConditionNumber;
        pregnantSlotCondition = binding.textViewPregnantSlotConditionNumber;
        chargingSlotCondition = binding.textViewChargingSlotConditionNumber;
        disabledSlotCondition = binding.textViewDisabledSlotConditionNumber;
        reservedSlotCondition = binding.textViewRegularReservationSlotConditionNumber;

        carSlotSetting=binding.editTextCarSetting;
        pregnantSlotSetting=binding.editTextPregnantSetting;
        disabledSlotSetting=binding.editTextDisabledSetting;
        chargingSlotSetting=binding.editTextChargingSetting;
        reservedSlotSetting=binding.editTextReservedSetting;

        carSlotLeftSetting=binding.editTextCarLeftSetting;
        pregnantSlotLeftSetting=binding.editTextPregnantLeftSetting;
        disabledSlotLeftSetting=binding.editTextDisabledLeftSetting;
        chargingSlotLeftSetting=binding.editTextChargingLeftSetting;

        carSlotSetting.setText("0");
        pregnantSlotSetting.setText("0");
        disabledSlotSetting.setText("0");
        chargingSlotSetting.setText("0");
        reservedSlotSetting.setText("0");

        carSlotLeftSetting.setText("0");
        pregnantSlotLeftSetting.setText("0");
        disabledSlotLeftSetting.setText("0");
        chargingSlotLeftSetting.setText("0");

        mViewModel.getCarSlotCondition().observe(getViewLifecycleOwner(), carSlotCondition::setText);
        mViewModel.getPregnantSlotCondition().observe(getViewLifecycleOwner(), pregnantSlotCondition::setText);
        mViewModel.getChargingSlotCondition().observe(getViewLifecycleOwner(), chargingSlotCondition::setText);
        mViewModel.getDisabledSlotCondition().observe(getViewLifecycleOwner(), disabledSlotCondition::setText);
        mViewModel.getReservedSlotCondition().observe(getViewLifecycleOwner(), reservedSlotCondition::setText);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CarSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}