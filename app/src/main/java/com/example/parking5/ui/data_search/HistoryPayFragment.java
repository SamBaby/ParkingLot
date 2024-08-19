package com.example.parking5.ui.data_search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryPayBinding;
import com.example.parking5.datamodel.PayHistory;
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

/***
 * shows all the cars already pay and are allowed to out
 */
public class HistoryPayFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 2, 2, 1, 2, 1};
    private HistoryPayViewModel mViewModel;
    private FragmentHistoryPayBinding binding;
    Vector<PayHistory> histories;
    private Button btnSearch;
    private Button btnPrevious;
    private Button btnNext;
    private int tableIndex = 0;

    public static HistoryPayFragment newInstance() {
        return new HistoryPayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryPayViewModel.class);

        binding = FragmentHistoryPayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        histories = new Vector<>();
        btnSearch = root.findViewById(R.id.button_search);
        btnSearch.setOnClickListener(v -> {
            searchHistory();
        });
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
        tableSetting();
        return root;
    }

    private void searchHistory() {
        final View dialogView = View.inflate(getActivity(), R.layout.pay_search, null);
        Dialog dialog = new Dialog(getActivity());
        Var<String> payment = new Var<>("");
        EditText txtNumber = dialogView.findViewById(R.id.car_number_edittext);
        TextView txtStart = dialogView.findViewById(R.id.textView_start);
        TextView txtEnd = dialogView.findViewById(R.id.textView_end);
        ToggleButton buttonDay = dialogView.findViewById(R.id.toggleButton_day);
        ToggleButton buttonWeek = dialogView.findViewById(R.id.toggleButton_week);
        ToggleButton buttonMonth = dialogView.findViewById(R.id.toggleButton_month);
        ToggleButton buttonDef = dialogView.findViewById(R.id.toggleButton_self_def);
        ToggleButton buttonCash = dialogView.findViewById(R.id.toggleButton_cash);
        ToggleButton buttonEPay = dialogView.findViewById(R.id.toggleButton_EPay);
        buttonDay.setChecked(true);
        buttonDay.setOnClickListener((v) -> {
            buttonWeek.setChecked(false);
            buttonMonth.setChecked(false);
            buttonDef.setChecked(false);
        });
        buttonWeek.setOnClickListener((v) -> {
            buttonDay.setChecked(false);
            buttonMonth.setChecked(false);
            buttonDef.setChecked(false);
        });
        buttonMonth.setOnClickListener((v) -> {
            buttonDay.setChecked(false);
            buttonWeek.setChecked(false);
            buttonDef.setChecked(false);
        });
        buttonDef.setOnClickListener((v) -> {
            buttonDay.setChecked(false);
            buttonWeek.setChecked(false);
            buttonMonth.setChecked(false);
        });
        buttonCash.setOnClickListener((v) -> {
            boolean c = buttonCash.isChecked();
            boolean e = buttonEPay.isChecked();
            payment.set(getPayment(c, e));
        });
        buttonEPay.setOnClickListener((v) -> {
            boolean c = buttonCash.isChecked();
            boolean e = buttonEPay.isChecked();
            payment.set(getPayment(c, e));
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

            tableSetting(start, end, txtNumber.getText().toString(), payment.get());
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private String getPayment(boolean cash, boolean EPay) {
        if (cash && !EPay) {
            return "C";
        } else if (!cash && EPay) {
            return "E";
        } else {
            return "";
        }
    }

    private void tableSetting() {
        tableSetting("", "", "", "");
    }

    private void tableSetting(String start, String end, String carNumber, String payment) {
        getHistoryWithDates(start, end, carNumber, payment);
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
        TableLayout tableData = binding.tablePayData;
        tableData.removeAllViews();
        // 遍历数据列表并为每行创建 TableRow
        int max = tableIndex * UtilParam.tableItemCount + UtilParam.tableItemCount;
        max = Math.min(max, histories.size());
        for (int i = tableIndex * UtilParam.tableItemCount; i < max; i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            PayHistory history = histories.get(i);
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
                        textView.setText(history.getTime_pay());
                        break;
                    case 3:
                        textView.setText(String.valueOf(history.getCost()));
                        break;
                    case 4:
                        textView.setText(history.getBill_number());
                        break;
                    case 5:
                        textView.setText(history.getPayment());
                        break;
                    default:
                        break;
                }
                tableRow.addView(textView);
            }

            // 将 TableRow 添加到 TableLayout
            tableData.addView(tableRow);
        }
    }

    private void getHistoryWithDates(String start, String end, String carNumber, String payment) {
        Thread t = new Thread(() -> {
            String json = "";
            if (!start.isEmpty() || !carNumber.isEmpty() || !payment.isEmpty()) {
                json = ApacheServerRequest.getPayHistoryWithDates(start, end, carNumber, payment);
            } else {
                json = ApacheServerRequest.getPayHistory();
            }

            try {
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    PayHistory history = gson.fromJson(obj.toString(), PayHistory.class);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryPayViewModel.class);
        // TODO: Use the ViewModel
    }

}