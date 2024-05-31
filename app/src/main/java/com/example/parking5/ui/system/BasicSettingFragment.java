package com.example.parking5.ui.system;

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
import android.widget.Switch;
import android.widget.TextView;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentAccountSettingBinding;
import com.example.parking5.databinding.FragmentBasicSettingBinding;
import com.example.parking5.datamodel.BasicSetting;
import com.example.parking5.datamodel.PayHistory;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

public class BasicSettingFragment extends Fragment {

    private BasicSettingViewModel mViewModel;
    private FragmentBasicSettingBinding binding;

    public static BasicSettingFragment newInstance() {
        return new BasicSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(BasicSettingViewModel.class);

        binding = FragmentBasicSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BasicSetting setting = getInformationFromServer();
        EditText txtLotName = binding.editTextParkingLotNameSetting;
        EditText txtCompanyName = binding.editTextCompanyNameSetting;
        EditText txtCompanyAddress = binding.editTextCompanyAddressSetting;
        EditText txtCompanyPhone = binding.editTextPhoneNumberSetting;
        EditText txtServerToken = binding.editTextServerTokenSetting;
        EditText txtChatID = binding.editTextChatIdSetting;
        EditText txtStandByPath = binding.editTextStandbyPathSetting;
        EditText txtStandByInterval = binding.editTextStandbyPlatIntervalSetting;
        Switch switchUploadLot = binding.switchLotsUpdateCHT;
        Switch switchStandByPlay = binding.switchPlayStandby;
        if (setting != null) {
            txtLotName.setText(setting.getLot_name());
            txtCompanyName.setText(setting.getCompany_name());
            txtCompanyAddress.setText(setting.getCompany_address());
            txtCompanyPhone.setText(setting.getCompany_phone());
            txtServerToken.setText(setting.getServer_token());
            txtChatID.setText(setting.getCht_chat_id());
            txtStandByPath.setText(setting.getStandby_path());
            txtStandByInterval.setText(String.valueOf(setting.getStandby_sec()));
            switchUploadLot.setChecked(setting.getAuto_upload_server() == 1);
            switchStandByPlay.setChecked(setting.getStandby_play() == 1);
        }
        Button btnSave = binding.buttonSave;
        btnSave.setOnClickListener(v -> {
            updateInfo(new BasicSetting(txtLotName.getText().toString(),
                    txtCompanyName.getText().toString(),
                    txtCompanyAddress.getText().toString(),
                    txtCompanyPhone.getText().toString(),
                    txtServerToken.getText().toString(),
                    txtChatID.getText().toString(),
                    txtStandByPath.getText().toString(),
                    Integer.parseInt(txtStandByInterval.getText().toString()),
                    switchUploadLot.isChecked() ? 1 : 0,
                    switchStandByPlay.isChecked() ? 1 : 0));
        });
        return root;
    }

    private void updateInfo(BasicSetting setting) {
        try {
            Thread t = new Thread(() -> ApacheServerReqeust.setCompanyInformation(setting));
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BasicSetting getInformationFromServer() {
        Var<BasicSetting> setting = new Var<>();
        Thread t = new Thread(() -> {
            String json = ApacheServerReqeust.getCompanyInformation();
            try {
                if (json != null) {
                    JSONArray jsonArray = new JSONArray(json);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    setting.set(gson.fromJson(jsonArray.getJSONObject(0).toString(), BasicSetting.class));
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
        return setting.get();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BasicSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}