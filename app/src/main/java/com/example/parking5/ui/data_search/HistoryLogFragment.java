package com.example.parking5.ui.data_search;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryEntranceBinding;
import com.example.parking5.databinding.FragmentHistoryLogBinding;
import com.example.parking5.datamodel.CarHistory;
import com.example.parking5.datamodel.ServerLog;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.util.Util;
import com.example.parking5.util.UtilParam;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class HistoryLogFragment extends Fragment {
    private static final int[] tableWeight = new int[]{1, 3};
    private FragmentHistoryLogBinding binding;
    private Button btnSearch;
    private Button btnDelete;
    private Button btnPrevious;
    private Button btnNext;
    private int tableIndex = 0;
    Var<TableRow> selectedRow = new Var<>();
    Vector<ServerLog> histories;
    private String startDate;
    private String endDate;
    private HistoryLogViewModel mViewModel;

    public static HistoryLogFragment newInstance() {
        return new HistoryLogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryLogViewModel.class);

        binding = FragmentHistoryLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedRow = new Var<>();
        histories = new Vector<>();

        btnDelete = root.findViewById(R.id.button_delete);
        btnSearch = root.findViewById(R.id.button_search);
        btnPrevious = root.findViewById(R.id.button_previous);
        btnNext = root.findViewById(R.id.button_next);
        btnPrevious.setOnClickListener(v -> {
            tableIndex--;
            refreshButtons();
            tableRefresh();
        });
        btnNext.setOnClickListener(v -> {
            tableIndex++;
            refreshButtons();
            tableRefresh();
        });
        btnDelete.setOnClickListener(v -> {
//            deleteHistory();
            final View dialogView = View.inflate(this.getContext(), R.layout.date_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
            DatePicker picker = (DatePicker) dialogView.findViewById(R.id.date_picker);
            picker.updateDate(2000, 0, 1);
            dialogView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int month = picker.getMonth() + 1;
                    deleteHistoryData(month);
                    alertDialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            // 隱藏日期選項，只顯示月份和年份
            dialogView.findViewById(
                    dialogView.getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            dialogView.findViewById(
                    dialogView.getResources().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
            alertDialog.setView(dialogView);
            alertDialog.show();
        });
        btnSearch.setOnClickListener(v -> {
            searchHistory();
        });

//        String start = "";
//        String end = "";
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
//        Calendar c = new GregorianCalendar();
//        start = String.format(formatter.format(c.getTime()) + " 00:00:00");
//        end = String.format(formatter.format(c.getTime()) + " 23:59:59");
//        tableSetting(start, end);
        tableSetting("", "");
        return root;
    }

    private void searchHistory() {
        final View dialogView = View.inflate(getActivity(), R.layout.log_search, null);
        Dialog dialog = new Dialog(getActivity());

        TextView txtStart = dialogView.findViewById(R.id.textView_start);
        TextView txtEnd = dialogView.findViewById(R.id.textView_end);
        ToggleButton buttonDay = dialogView.findViewById(R.id.toggleButton_day);
        ToggleButton buttonWeek = dialogView.findViewById(R.id.toggleButton_week);
        ToggleButton buttonMonth = dialogView.findViewById(R.id.toggleButton_month);
        ToggleButton buttonDef = dialogView.findViewById(R.id.toggleButton_self_def);
        buttonDay.setChecked(true);
        buttonDay.setOnClickListener((v) -> {
            if (buttonWeek.isChecked() || buttonMonth.isChecked() || buttonDef.isChecked()) {
                buttonWeek.setChecked(false);
                buttonMonth.setChecked(false);
                buttonDef.setChecked(false);
            } else {
                buttonDay.setChecked(true);
            }
        });
        buttonWeek.setOnClickListener((v) -> {
            if (buttonDay.isChecked() || buttonMonth.isChecked() || buttonDef.isChecked()) {
                buttonDay.setChecked(false);
                buttonMonth.setChecked(false);
                buttonDef.setChecked(false);
            } else {
                buttonWeek.setChecked(true);
            }

        });
        buttonMonth.setOnClickListener((v) -> {
            if (buttonDay.isChecked() || buttonWeek.isChecked() || buttonDef.isChecked()) {
                buttonDay.setChecked(false);
                buttonWeek.setChecked(false);
                buttonDef.setChecked(false);
            } else {
                buttonMonth.setChecked(true);
            }
        });
        buttonDef.setOnClickListener((v) -> {
            if (buttonDay.isChecked() || buttonWeek.isChecked() || buttonMonth.isChecked()) {
                buttonDay.setChecked(false);
                buttonWeek.setChecked(false);
                buttonMonth.setChecked(false);
            } else {
                buttonDef.setChecked(true);
            }
        });
        txtStart.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtStart);
        });
        txtEnd.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtEnd);
        });
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String start = "";
            String end = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
            Calendar c = new GregorianCalendar();
            if (buttonDay.isChecked()) {
                start = String.format(formatter.format(c.getTime()) + " 00:00:00");
                end = String.format(formatter.format(c.getTime()) + " 23:59:59");
            } else if (buttonWeek.isChecked()) {
                // get start of this week in milliseconds
                c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                start = String.format(formatter.format(c.getTime()) + " 00:00:00");
                // start of the next week
                c.add(Calendar.WEEK_OF_YEAR, 1);
                c.add(Calendar.DAY_OF_YEAR, -1);
                end = String.format(formatter.format(c.getTime()) + " 23:59:59");
            } else if (buttonMonth.isChecked()) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                start = String.format(formatter.format(c.getTime()) + " 00:00:00");
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                end = String.format(formatter.format(c.getTime()) + " 23:59:59");
            } else if (buttonDef.isChecked()) {
                start = txtStart.getText().toString();
                end = txtEnd.getText().toString();
            }
            if (!start.isEmpty() && !end.isEmpty()) {
                tableSetting(start, end);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void tableSetting() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
        Calendar c = new GregorianCalendar();
        String start = String.format(formatter.format(c.getTime()) + " 00:00:00");
        String end = String.format(formatter.format(c.getTime()) + " 23:59:59");
        tableSetting(start, end);
    }

    private void tableSetting(String start, String end) {
        getHistoryWithDates(start, end);
        tableIndex = 0;
        refreshButtons();
        tableRefresh();
    }

    private void refreshButtons() {
        if (tableIndex == 0) {
            btnPrevious.setVisibility(View.INVISIBLE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }
        if (tableIndex * UtilParam.tableItemCount + UtilParam.tableItemCount >= histories.size()) {
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void tableRefresh() {
        TableLayout tableData = binding.tableLogData;
        tableData.removeAllViews();
        selectedRow.set(null);
        // 遍历数据列表并为每行创建 TableRow
        int max = tableIndex * UtilParam.tableItemCount + UtilParam.tableItemCount;
        max = Math.min(max, histories.size());
        for (int i = tableIndex * UtilParam.tableItemCount; i < max; i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            ServerLog history = histories.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 2; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        String time = history.getTime();
                        if (history.getUrl() != null && !history.getUrl().isEmpty()) {
                            time = "*" + time;
                        }
                        textView.setText(time);
                        break;
                    case 1:
                        textView.setText(history.getDescription());
                        break;
                    default:
                        break;
                }
                tableRow.addView(textView);

            }
            tableRow.setOnClickListener(v -> {
                if (selectedRow.get() != null) {
                    selectedRow.get().setBackground(null);
                }
                v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.border));
                selectedRow.set((TableRow) v);
            });
            if (history.getUrl() != null && !history.getUrl().isEmpty()) {
                tableRow.setOnLongClickListener(v -> {
                    // Load the image and set it to the ImageView
                    String path = history.getUrl();
                    Bitmap bitmap = ApacheServerRequest.getPictureByPath(path);
                    if (bitmap != null) {
                        showImageDialog(bitmap);
                    }
                    return false;
                });
            }
            // 将 TableRow 添加到 TableLayout
            tableData.addView(tableRow);
        }
    }

    private void getHistory20() {
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getLogs20();
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    ServerLog history = gson.fromJson(obj.toString(), ServerLog.class);
                    histories.add(history);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHistory() {
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getLogs();
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    ServerLog history = gson.fromJson(obj.toString(), ServerLog.class);
                    histories.add(history);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHistoryWithDates(String start, String end) {
        Thread t = new Thread(() -> {
            String json = "";
            if (!start.isEmpty() && !end.isEmpty()) {
                json = ApacheServerRequest.getLogsWithDates(start, end);
            } else {
                json = ApacheServerRequest.getLogs20();
            }

            try {
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    ServerLog history = gson.fromJson(obj.toString(), ServerLog.class);
                    histories.add(history);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteHistoryData(long id) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.deleteHistory(id);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteHistoryData(int month) {
        Thread t = new Thread(() -> {
            // 獲取當前日期
            Calendar calendar = Calendar.getInstance();

            // 向前移動一個月
            calendar.add(Calendar.MONTH, month * -1);

            // 格式化日期輸出
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String oneMonthAgo = dateFormat.format(calendar.getTime());

            ApacheServerRequest.deleteLog(oneMonthAgo);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryLogViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showImageDialog(Bitmap bitmap) {
        Dialog imageDialog = new Dialog(this.getContext());
        imageDialog.setContentView(R.layout.dialog_image);
        ImageView imageView = imageDialog.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);// Set your image here

        imageDialog.setOnDismissListener(dialog -> {
            // Handle the dialog dismissing
        });

        imageDialog.setCanceledOnTouchOutside(true);

        imageDialog.show();
    }
}