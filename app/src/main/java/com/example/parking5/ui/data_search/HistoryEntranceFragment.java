package com.example.parking5.ui.data_search;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentDataSearchBinding;
import com.example.parking5.databinding.FragmentHistoryEntranceBinding;
import com.example.parking5.util.CustomTimePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class HistoryEntranceFragment extends Fragment {

    private HistoryEntranceViewModel mViewModel;
    private FragmentHistoryEntranceBinding binding;
    private Button btnAdd;
    private Button btnSearch;
    private Button btnModify;

    public static HistoryEntranceFragment newInstance() {
        return new HistoryEntranceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(HistoryEntranceViewModel.class);

        binding = FragmentHistoryEntranceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAdd= root.findViewById(R.id.button_add);
        btnModify = root.findViewById(R.id.button_modify);
        btnSearch = root.findViewById(R.id.button_search);
        btnAdd.setOnClickListener(v->{

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
//            DatePickerDialog dialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,null,year, month ,day );
//            try {
//                TimePickerDialog dialog = new
//                dialog.show();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            TimePicker picker=(TimePicker)dialogView.findViewById(R.id.time_picker);
            picker.setIs24HourView(true);
            dialogView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                    Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());

                    long time = calendar.getTimeInMillis();
                    alertDialog.dismiss();
                }});
            dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        });
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryEntranceViewModel.class);
        // TODO: Use the ViewModel
    }

}