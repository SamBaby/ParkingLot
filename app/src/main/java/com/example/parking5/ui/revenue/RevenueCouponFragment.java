package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.parking5.R;
import com.example.parking5.data.LoginRepository;
import com.example.parking5.databinding.FragmentRevenueCouponBinding;
import com.example.parking5.datamodel.CouponHistory;
import com.example.parking5.datamodel.User;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/***
 * modification and history of the coupon settings
 */
public class RevenueCouponFragment extends Fragment {
    private static final int[] tableWeight = new int[]{1, 1, 1, 2, 1, 1};
    private RevenueCouponViewModel mViewModel;
    Var<TableRow> selectedRow = new Var<>();
    private Vector<CouponHistory> histories = new Vector<>();

    public static RevenueCouponFragment newInstance() {
        return new RevenueCouponFragment();
    }

    private FragmentRevenueCouponBinding binding;
    private Button btnPrint;
    private ToggleButton btnTime;
    private ToggleButton btnMoney;
    private EditText txtDiscountHour;
    private TextView txtDeadlineHour;
    private EditText txtDiscountMoney;
    private TextView txtDeadlineMoney;
    private EditText txtPiece;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRevenueCouponBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnPrint = binding.buttonPrint;

        btnPrint.setOnClickListener(v -> {
            printCoupon();
        });
        tableSetting();
        buttonSetting();
        return root;
    }

    private void buttonSetting() {
        btnTime = binding.toggleButtonDiscountTime;
        txtDiscountHour = binding.editTextDiscountTime;
        txtDeadlineHour = binding.editTextDiscountTimeDeadline;

        btnMoney = binding.toggleButtonDiscountFee;
        txtDiscountMoney = binding.editTextDiscountFee;
        txtDeadlineMoney = binding.editTextDiscountFeeDeadline;

        txtPiece = binding.editTextPrintPiece;
        txtDeadlineHour.setOnClickListener(v -> {
            showDateDialog(txtDeadlineHour);
        });
        txtDeadlineMoney.setOnClickListener(v -> {
            showDateDialog(txtDeadlineMoney);
        });
        btnTime.setOnClickListener(v -> {
            if (btnMoney.isChecked()) {
                btnMoney.setChecked(false);
            } else {
                btnTime.setChecked(true);
            }
        });
        btnMoney.setOnClickListener(v -> {
            if (btnTime.isChecked()) {
                btnTime.setChecked(false);
            } else {
                btnMoney.setChecked(true);
            }
        });
    }

    private void showDateDialog(TextView v) {
        Util.showDateDialog(getActivity(), v);
    }

    private void printCoupon() {
        int timeFee = 0;
        Var<String> amount = new Var<>("");
        Var<String> deadline = new Var<>("");
        Var<String> paper = new Var<>("");
        if (btnTime.isChecked()) {
            amount.set(txtDiscountHour.getText().toString());
            deadline.set(txtDeadlineHour.getText().toString());
        } else {
            amount.set(txtDiscountMoney.getText().toString());
            deadline.set(txtDeadlineMoney.getText().toString());
            timeFee = 1;
        }
        paper.set(txtPiece.getText().toString());
        if (!amount.get().isEmpty() && !deadline.get().isEmpty() && !paper.get().isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
            Date d = new Date();
            String timeCode = formatter.format(d);

            addCouponHistory(timeFee, amount.get(), paper.get(), deadline.get());
            updateCouponSetting(timeFee, amount.get(), paper.get(), deadline.get(), timeCode);
            updateCouponList(timeCode, deadline.get(), paper.get(), timeFee, amount.get());
            tableSetting();
        }
    }

    private void tableSetting() {
        TableLayout tableData = binding.tableCouponData;
        tableData.removeAllViews();
        selectedRow.set(null);
        getCouponData();
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < histories.size(); i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            CouponHistory history = histories.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 6; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textView.setText(String.valueOf(history.getId()));
                        break;
                    case 1:
                        textView.setText(getAmountString(history));
                        break;
                    case 2:
                        textView.setText(String.valueOf(history.getCount()));
                        break;
                    case 3:
                        textView.setText(history.getDeadline());
                        break;
                    case 4:
                        textView.setText(history.getUser());
                        break;
                    case 5:
                        textView.setText(history.getMark());
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

    private String getAmountString(CouponHistory history) {
        String ret = String.valueOf(history.getAmount());
        if (history.getTime_fee() == 0) {
            ret += getString(R.string.hour);
        } else {
            ret += getString(R.string.dollar);
        }
        return ret;
    }

    private void getCouponData() {
        histories.clear();
        Thread t = new Thread(() -> {
            String json = ApacheServerRequest.getCouponHistory();

            try {
                JSONArray array = new JSONArray(json);
                histories.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    CouponHistory history = gson.fromJson(obj.toString(), CouponHistory.class);
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

    private void addCouponHistory(int timeOrFee, String amount, String count, String deadline) {
        Thread t = new Thread(() -> {
            User user = LoginRepository.getInstance().getLoggedInUser();
            ApacheServerRequest.addCouponHistory(String.valueOf(timeOrFee), amount, count, deadline, user.getName(), "");
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCouponSetting(int timeOrFee, String amount, String count, String deadline, String timeCode) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.updateCouponSetting(String.valueOf(timeOrFee), amount, count, deadline, timeCode);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCouponList(String timeCode, String deadline, String count, int timeOrFee, String amount) {
        Thread t = new Thread(() -> {
            int len = Integer.parseInt(count);
            for (int i = 0; i < len; i++) {
                ApacheServerRequest.addCouponList(String.format("%s_%s", timeCode, count), deadline, timeOrFee, amount);
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
        mViewModel = new ViewModelProvider(this).get(RevenueCouponViewModel.class);
        // TODO: Use the ViewModel
    }

}