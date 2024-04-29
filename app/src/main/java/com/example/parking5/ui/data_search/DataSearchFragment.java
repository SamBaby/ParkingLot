package com.example.parking5.ui.data_search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.BasicSettingFragment;
import com.example.parking5.R;
import com.example.parking5.databinding.FragmentDataSearchBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DataSearchFragment extends Fragment {

    private DataSearchViewModel mViewModel;
    private FragmentDataSearchBinding binding;

    public static DataSearchFragment newInstance() {
        return new DataSearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(DataSearchViewModel.class);

        binding = FragmentDataSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_data_search);
        // Set the default fragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new HistoryEntranceFragment())
                .commit();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_history_view) {
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, new HistoryEntranceFragment(), null).commit();
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
        mViewModel = new ViewModelProvider(this).get(DataSearchViewModel.class);
        // TODO: Use the ViewModel
    }

}