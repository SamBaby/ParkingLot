package com.example.parking5.ui.system;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.databinding.FragmentHolidayBinding;

public class HolidayFragment extends Fragment {

    private HolidayViewModel mViewModel;
    private FragmentHolidayBinding binding;

    public static HolidayFragment newInstance() {
        return new HolidayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(HolidayViewModel.class);

        binding = FragmentHolidayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TableLayout tableHolidayData = binding.tableHolidayData;
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < 40; i++) {
            TableRow tableRow = new TableRow(tableHolidayData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // 为每行添加单元格
            for (int j = 0; j < 6; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setText(String.valueOf(i));
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                tableRow.addView(textView);
            }

            // 将 TableRow 添加到 TableLayout
            tableHolidayData.addView(tableRow);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HolidayViewModel.class);
        // TODO: Use the ViewModel
    }

}