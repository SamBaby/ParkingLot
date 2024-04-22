package com.example.parking5.ui.revenue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentBasicFeeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BasicFeeFragment extends Fragment {

    private FragmentBasicFeeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BasicFeeViewModel slideshowViewModel =
                new ViewModelProvider(this).get(BasicFeeViewModel.class);

        binding = FragmentBasicFeeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_system);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_basic_fee) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new BasicFeeFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_car_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new CarSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_print_setting) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new PrintSettingFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_revenue_manage) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new RevenueManageFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_revenue_coupon) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new RevenueCouponFragment(), null).commit();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}