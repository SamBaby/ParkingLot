package com.example.parking5.ui.system;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHolidayBinding;
import com.example.parking5.datamodel.Holiday;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class HolidayFragment extends Fragment {
    private static final int[] tableWeight = new int[]{1, 2, 1, 2, 2, 2};
    private static final String[] weekdays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private HolidayViewModel mViewModel;
    private FragmentHolidayBinding binding;

    public static HolidayFragment newInstance() {
        return new HolidayFragment();
    }

    Vector<Holiday> holidays;
    Var<TableRow> selectedRow;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(HolidayViewModel.class);

        binding = FragmentHolidayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        holidays = new Vector<>();
        selectedRow = new Var<>();
        tableSetting();

        Button btnAdd = binding.buttonHolidayAdd;
        Button btnModify = binding.buttonHolidayModify;
        Button btnDelete = binding.buttonHolidayDelete;
        btnAdd.setOnClickListener((v) -> {
            showHolidayAddDialog();
        });
        btnModify.setOnClickListener((v) -> {
            showHolidayUpdateDialog();
        });
        btnDelete.setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                String date = ((TextView) selectedRow.get().getChildAt(1)).getText().toString();
                deleteHoliday(date);
            }
        });
        return root;
    }

    private void deleteHoliday(String date) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.deleteHoliday(date);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void showHolidayUpdateDialog() {
        if (selectedRow.get() != null) {
            final View dialogView = View.inflate(getActivity(), R.layout.holiday_setting, null);
            Dialog dialog = new Dialog(getActivity());
            ((TextView) dialogView.findViewById(R.id.date_edittext)).setOnClickListener(v -> {
                try {
                    Util.showDateDialog(getActivity(), (TextView) v);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            String oldDate = ((TextView) selectedRow.get().getChildAt(1)).getText().toString();
            ((TextView) dialogView.findViewById(R.id.number_edittext)).setText(((TextView) selectedRow.get().getChildAt(0)).getText().toString());
            ((TextView) dialogView.findViewById(R.id.date_edittext)).setText(((TextView) selectedRow.get().getChildAt(1)).getText().toString());
            ((TextView) dialogView.findViewById(R.id.desc_edittext)).setText(((TextView) selectedRow.get().getChildAt(3)).getText().toString());
            dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
                try {
                    String number = ((TextView) dialogView.findViewById(R.id.number_edittext)).getText().toString();
                    String date = ((TextView) dialogView.findViewById(R.id.date_edittext)).getText().toString();
                    String desc = ((TextView) dialogView.findViewById(R.id.desc_edittext)).getText().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                    Date d = new Date();
                    String updateDate = formatter.format(d);

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(formatter.parse(date));
                    int weekday = calendar.get(Calendar.DAY_OF_WEEK);

                    if (!number.isEmpty() && !date.isEmpty() && !desc.isEmpty()) {
                        updateHoliday(Integer.parseInt(number), oldDate, date, desc, weekday, updateDate, "parkjohn");
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }

    private void updateHoliday(int number, String oldDate, String newDate, String desc, int weekday, String updateDate, String account) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.updateHoliday(number, oldDate, newDate, weekday, desc, updateDate, account);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void showHolidayAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.holiday_setting, null);
        Dialog dialog = new Dialog(getActivity());
        ((TextView) dialogView.findViewById(R.id.date_edittext)).setOnClickListener(v -> {
            Util.showDateDialog(getActivity(), (TextView) v);
        });

        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            try {
                String number = ((TextView) dialogView.findViewById(R.id.number_edittext)).getText().toString();
                String date = ((TextView) dialogView.findViewById(R.id.date_edittext)).getText().toString();
                String desc = ((TextView) dialogView.findViewById(R.id.desc_edittext)).getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                Date d = new Date();
                String updateDate = formatter.format(d);

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(formatter.parse(date));
                int weekday = calendar.get(Calendar.DAY_OF_WEEK);

                if (!number.isEmpty() && !date.isEmpty() && !desc.isEmpty()) {
                    addHoliday(Integer.parseInt(number), date, desc, weekday, updateDate, "parkjohn");
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void addHoliday(int number, String date, String desc, int weekday, String updateDate, String account) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.addHoliday(number, date, weekday, desc, updateDate, account);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void tableSetting() {
        TableLayout tableHoliday = binding.tableHolidayData;
        tableHoliday.removeAllViews();
        selectedRow.set(null);
        new Thread(() -> {
            getHolidays();
            getActivity().runOnUiThread(() -> {
                try {
                    // 遍历数据列表并为每行创建 TableRow
                    for (int i = 0; i < holidays.size(); i++) {
                        TableRow tableRow = new TableRow(tableHoliday.getContext());
                        tableRow.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        Holiday holiday = holidays.get(i);
                        // 为每行添加单元格
                        for (int j = 0; j < 6; j++) {
                            TextView textView = new TextView(tableRow.getContext());
                            textView.setPadding(5, 5, 5, 5);
                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                            textView.setGravity(Gravity.CENTER);
                            textView.setLayoutParams(layoutParams);
                            switch (j) {
                                case 0:
                                    textView.setText(String.valueOf(holiday.getNumber()));
                                    break;
                                case 1:
                                    textView.setText(holiday.getDate());
                                    break;
                                case 2:
                                    textView.setText(weekdays[holiday.getWeekday()]);
                                    break;
                                case 3:
                                    textView.setText(holiday.getDescription());
                                    break;
                                case 4:
                                    textView.setText(holiday.getUpdateDate());
                                    break;
                                case 5:
                                    textView.setText(holiday.getAccount());
                                    break;
                                default:
                                    break;
                            }
                            tableRow.addView(textView);
                            tableRow.setOnClickListener(v -> {
                                if (selectedRow.get() != null) {
                                    selectedRow.get().setBackground(null);
                                }
                                v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.border));
                                selectedRow.set((TableRow) v);
                            });
                        }
                        // 将 TableRow 添加到 TableLayout
                        tableHoliday.addView(tableRow);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private void getHolidays() {
        try {
            String json = ApacheServerReqeust.getHolidays();
            JSONArray array = new JSONArray(json);
            if (array.length() > 0) {
                holidays.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    holidays.add(new Holiday(obj.getInt("number"), obj.getString("date"),
                            obj.getInt("weekday"), obj.getString("update_date"), obj.getString("account"), obj.getString("description")));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HolidayViewModel.class);
        // TODO: Use the ViewModel
    }

}