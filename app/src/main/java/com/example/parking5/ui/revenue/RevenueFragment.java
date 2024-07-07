package com.example.parking5.ui.revenue;

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
import com.example.parking5.data.ConfigurationString;
import com.example.parking5.databinding.FragmentRevenueBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RevenueFragment extends Fragment {

    private FragmentRevenueBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RevenueViewModel slideshowViewModel =
                new ViewModelProvider(this).get(RevenueViewModel.class);

        binding = FragmentRevenueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_revenue);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_revenue, new BasicFeeFragment())
                .commit();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_basic_fee) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_revenue, new BasicFeeFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_car_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_revenue, new CarSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_print_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_revenue, new PrintSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_revenue_manage) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_revenue, new RevenueManageFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_revenue_coupon) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_revenue, new RevenueCouponFragment(), null).commit();
                    return true;
                }
                return false;
            }
        });
        Bundle bundle = this.getArguments();
        if(bundle !=null){
            if(bundle.getBoolean(ConfigurationString.todayRevenue)){
                navigation.setSelectedItemId(R.id.nav_revenue_manage);
            }else if(bundle.getBoolean(ConfigurationString.carSetting)){
                navigation.setSelectedItemId(R.id.nav_car_setting);
            }else if(bundle.getBoolean(ConfigurationString.billLeft)){
                navigation.setSelectedItemId(R.id.nav_print_setting);
            }
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}