package com.example.parking5.ui.system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.parking5.BasicSettingFragment;
import com.example.parking5.R;
import com.example.parking5.databinding.FragmentSystemSettingBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SystemSettingFragment extends Fragment {

    private SystemSettingViewModel systemSettingViewModel;
    private FragmentSystemSettingBinding binding;

    public static SystemSettingFragment newInstance() {
        return new SystemSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        systemSettingViewModel =
                new ViewModelProvider(this).get(SystemSettingViewModel.class);

        binding = FragmentSystemSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_system);
        // Set the default fragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_system_setting, new BasicSettingFragment())
                .commit();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_basic_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_system_setting, new BasicSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_account_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_system_setting, new AccountSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_camera_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_system_setting, new CameraSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_bill_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_system_setting, new BillFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_holiday_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_system_setting, new HolidayFragment(), null).commit();
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