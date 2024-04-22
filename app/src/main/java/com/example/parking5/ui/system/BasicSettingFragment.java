package com.example.parking5.ui.system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentBasicSettingBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BasicSettingFragment extends Fragment {

    private BasicSettingViewModel basicSettingViewModel;
    private FragmentBasicSettingBinding binding;

    public static BasicSettingFragment newInstance() {
        return new BasicSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        basicSettingViewModel =
                new ViewModelProvider(this).get(BasicSettingViewModel.class);

        binding = FragmentBasicSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_system);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_basic_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new BasicSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_account_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new AccountSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_camera_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new CameraSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_bill_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new BillFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_holiday_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HolidayFragment(), null).commit();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(BasicSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}