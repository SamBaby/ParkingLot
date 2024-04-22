package com.example.parking5.ui.data_search;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentBasicFeeBinding;
import com.example.parking5.databinding.FragmentHistoryBinding;
import com.example.parking5.ui.revenue.BasicFeeFragment;
import com.example.parking5.ui.revenue.BasicFeeViewModel;
import com.example.parking5.ui.revenue.CarSettingFragment;
import com.example.parking5.ui.revenue.PrintSettingFragment;
import com.example.parking5.ui.revenue.RevenueCouponFragment;
import com.example.parking5.ui.revenue.RevenueManageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mViewModel;
    private FragmentHistoryBinding binding;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        HistoryViewModel slideshowViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_system);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_history_view) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HistoryFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_history_pay) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HistoryPayFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_history_out) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HistoryOutFragment(), null).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.history_license) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HistoryLicenseFragment(), null).commit();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}