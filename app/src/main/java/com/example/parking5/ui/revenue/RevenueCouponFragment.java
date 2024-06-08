package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryEntranceBinding;
import com.example.parking5.databinding.FragmentRevenueCouponBinding;

public class RevenueCouponFragment extends Fragment {

    private RevenueCouponViewModel mViewModel;

    public static RevenueCouponFragment newInstance() {
        return new RevenueCouponFragment();
    }
    private FragmentRevenueCouponBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRevenueCouponBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tableSetting();
        return root;
    }

    private void tableSetting() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RevenueCouponViewModel.class);
        // TODO: Use the ViewModel
    }

}