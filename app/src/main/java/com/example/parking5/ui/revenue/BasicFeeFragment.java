package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.parking5.databinding.FragmentBasicFeeBinding;
import com.example.parking5.datamodel.BasicFee;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class BasicFeeFragment extends Fragment {

    private BasicFeeViewModel mViewModel;
    private FragmentBasicFeeBinding binding;

    public static BasicFeeFragment newInstance() {
        return new BasicFeeFragment();
    }

    Var<BasicFee> basicFee;
    private int oneHourUnit = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(BasicFeeViewModel.class);

        binding = FragmentBasicFeeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        basicFee = new Var<>();
        getData();
        EditText not_count_minute = binding.editTextNotCountMinute;
        EditText weekdayFee = binding.editTextWeekdayHourDollar;
        EditText weekdayMostFee = binding.editTextMostFeeADayWeekday;
        EditText weekendFee = binding.editTextWeekendHourDollar;
        EditText weekendMostFee = binding.editTextMostFeeADayWeekend;
        EditText selfDefText = binding.editTextSelfDef;
        Switch switchBeforeOneHour = binding.switchBeforeOneHour;
        Switch switchWeekdayWeekendCross = binding.switchCrossWeekdayWeekend;
        ToggleButton button30 = binding.toggleButtonThirtyMinuteCount;
        ToggleButton button60 = binding.toggleButtonOneHourCount;
        ToggleButton buttonSelf = binding.toggleButtonSelfDef;
        Button buttonSave = binding.buttonSave;
        if (basicFee.get() != null) {
            not_count_minute.setText(String.valueOf(basicFee.get().getEnter_time_not_count()));
            weekdayFee.setText(String.valueOf(basicFee.get().getWeekday_fee()));
            weekdayMostFee.setText(String.valueOf(basicFee.get().getWeekday_most_fee()));
            weekendFee.setText(String.valueOf(basicFee.get().getHoliday_fee()));
            weekendMostFee.setText(String.valueOf(basicFee.get().getHoliday_most_fee()));
            switchBeforeOneHour.setChecked(basicFee.get().getBefore_one_hour_count() == 1);
            switchWeekdayWeekendCross.setChecked(basicFee.get().getWeekday_holiday_cross() == 1);
            button30.setChecked(basicFee.get().getAfter_one_hour_unit() == 0);
            button60.setChecked(basicFee.get().getAfter_one_hour_unit() == 1);
            buttonSelf.setChecked(basicFee.get().getAfter_one_hour_unit() > 1);
            if (basicFee.get().getAfter_one_hour_unit() > 1) {
                selfDefText.setText(String.valueOf(basicFee.get().getAfter_one_hour_unit()));
            }
        }
        button30.setOnClickListener(v -> {
            if (oneHourUnit == 0 && !button30.isChecked()) {
                button30.setChecked(true);
                return;
            }
            if (button30.isChecked()) {
                button60.setChecked(false);
                buttonSelf.setChecked(false);
                oneHourUnit = 0;
            }
        });
        button60.setOnClickListener(v -> {
            if (oneHourUnit == 1 && !button60.isChecked()) {
                button60.setChecked(true);
                return;
            }
            if (button60.isChecked()) {
                button30.setChecked(false);
                buttonSelf.setChecked(false);
                oneHourUnit = 1;
            }
        });
        buttonSelf.setOnClickListener(v -> {
            if (oneHourUnit > 1 && !buttonSelf.isChecked()) {
                buttonSelf.setChecked(true);
                return;
            }
            if (buttonSelf.isChecked()) {
                button30.setChecked(false);
                button60.setChecked(false);
                if (!selfDefText.getText().toString().isEmpty()) {
                    oneHourUnit = Integer.parseInt(selfDefText.getText().toString());
                }
            }
        });
        selfDefText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(buttonSelf.isChecked() && !s.toString().isEmpty()){
                    int unit = Integer.parseInt(s.toString());
                    if(unit > 1){
                        oneHourUnit = unit;
                    }else{
                        Toast.makeText(getActivity(), "Must greater than 1", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        buttonSave.setOnClickListener(v -> {
            updateFee(new BasicFee(Integer.parseInt(not_count_minute.getText().toString()),
                    switchBeforeOneHour.isChecked() ? 1 : 0,
                    oneHourUnit,
                    Integer.parseInt(weekdayFee.getText().toString()),
                    Integer.parseInt(weekdayMostFee.getText().toString()),
                    Integer.parseInt(weekendFee.getText().toString()),
                    Integer.parseInt(weekendMostFee.getText().toString()),
                    switchWeekdayWeekendCross.isChecked() ? 1 : 0
            ));
        });
        return root;
    }

    private void getData() {
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getBasicFee();
            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        BasicFee fee = gson.fromJson(obj.toString(), BasicFee.class);
                        basicFee.set(fee);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFee(BasicFee fee) {
        try {
            Thread t = new Thread(() -> ApacheServerRequest.setBasicFee(fee));
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BasicFeeViewModel.class);
        // TODO: Use the ViewModel
    }

}