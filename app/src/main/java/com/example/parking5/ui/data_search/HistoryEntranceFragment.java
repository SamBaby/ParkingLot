package com.example.parking5.ui.data_search;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryEntranceBinding;
import com.example.parking5.datamodel.CarHistory;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class HistoryEntranceFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 3, 3, 1, 1, 1};
    private HistoryEntranceViewModel mViewModel;
    private FragmentHistoryEntranceBinding binding;
    private Button btnAdd;
    private Button btnSearch;
    private Button btnDelete;
    Var<TableRow> selectedRow = new Var<>();
    Vector<CarHistory> histories;
    private String startDate;
    private String endDate;

    public static HistoryEntranceFragment newInstance() {
        return new HistoryEntranceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryEntranceViewModel.class);

        binding = FragmentHistoryEntranceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedRow = new Var<>();
        histories = new Vector<>();

//        btnAdd = root.findViewById(R.id.button_add);
        btnDelete = root.findViewById(R.id.button_delete);
        btnSearch = root.findViewById(R.id.button_search);
//        btnAdd.setOnClickListener(v -> {
////            addHistory();
//        });
        btnDelete.setOnClickListener(v -> {
            deleteHistory();
        });
        btnSearch.setOnClickListener(v -> {
            searchHistory();
        });
        String start = "";
        String end = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
        Calendar c = new GregorianCalendar();
        start = String.format(formatter.format(c.getTime()) + " 00:00:00");
        end = String.format(formatter.format(c.getTime()) + " 23:59:59");
        tableSetting(start ,end);
        return root;
    }

    private void deleteHistory() {
        if (selectedRow.get() != null) {
            TableLayout table = binding.tableEntranceData;
            int index = table.indexOfChild(selectedRow.get());
            deleteHistoryData(histories.get(index).getId());
            tableSetting();
        }
    }

    private void searchHistory() {
        final View dialogView = View.inflate(getActivity(), R.layout.car_search, null);
        Dialog dialog = new Dialog(getActivity());

        EditText txtNumber = dialogView.findViewById(R.id.car_number_edittext);
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
        TableLayout tableData = binding.tableEntranceData;
        tableData.removeAllViews();
        selectedRow.set(null);
        getHistoryWithDates(start, end);
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < histories.size(); i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            CarHistory history = histories.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 6; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textView.setText(history.getCar_number());
                        break;
                    case 1:
                        textView.setText(history.getTime_in());
                        break;
                    case 2:
                        textView.setText(history.getTime_out());
                        break;
                    case 3:
                        textView.setText("小客");
                        break;
                    case 4:
                        textView.setText("白");
                        break;
                    case 5:
                        textView.setText(history.getArtificial() == 0 ? "否" : "是");
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
            tableRow.setOnLongClickListener(v -> {
                // Load the image and set it to the ImageView
//                Bitmap bitmap = BitmapFactory.decodeFile(history.getPicture_url());
                Bitmap bitmap = ApacheServerRequest.getPictureByPath(history.getPicture_url());
                if(bitmap != null){
                    showImageDialog(bitmap);
                }
                return false;
            });
            // 将 TableRow 添加到 TableLayout
            tableData.addView(tableRow);
        }
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

    private void getHistory() {
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getHistories();
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    CarHistory history = gson.fromJson(obj.toString(), CarHistory.class);
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
                json = ApacheServerRequest.getHistoriesWithDates(start, end);
            } else {
                json = ApacheServerRequest.getHistories();
            }

            try {
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    CarHistory history = gson.fromJson(obj.toString(), CarHistory.class);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryEntranceViewModel.class);
        // TODO: Use the ViewModel
    }

}