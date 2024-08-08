package com.example.parking5.ui.revenue;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentRevenueManageBinding;
import com.example.parking5.datamodel.MoneyBasic;
import com.example.parking5.datamodel.MoneyCount;
import com.example.parking5.datamodel.MoneySupply;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/***
 * shows revenue and all the coins and bills.
 */
public class RevenueManageFragment extends Fragment {

    private RevenueManageViewModel mViewModel;
    private FragmentRevenueManageBinding binding;
    TextView txtCashDay;
    TextView txtCashMonth;
    TextView txtECPayDay;
    TextView txtECPayMonth;
    TextView txtDay;
    TextView txtMonth;
    TextView amount5;
    TextView amount10;
    TextView amount50;
    TextView amount100;
    TextView count5;
    TextView count10;
    TextView count50;
    TextView count100;
    TextView countTotal;
    Button buttonTakeMoney;
    TextView basic5;
    TextView basic10;
    TextView basic50;
    TextView reminder5;
    TextView reminder10;
    TextView reminder50;
    Button btnBasicSetting;
    TextView input5;
    TextView input10;
    TextView input50;
    TextView refund5;
    TextView refund10;
    TextView refund50;
    Button btnInput;
    Button btnRefund;

    public static RevenueManageFragment newInstance() {
        return new RevenueManageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRevenueManageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        revenueSetting();
        moneyConditionSetting();
        moneyBasicSetting();
        moneyRefundSetting();
        refreshRevenue();
        return root;
    }

    private void moneyRefundSetting() {
        input5 = binding.fiveInput;
        input10 = binding.tenInput;
        input50 = binding.fiftyInput;
        refund5 = binding.fiveRefund;
        refund10 = binding.tenRefund;
        refund50 = binding.fiftyRefund;
        btnInput = binding.buttonInput;
        btnRefund = binding.buttonRefund;
        btnInput.setOnClickListener(v -> {
            showSupplyChooseDialog();
            setMoneyCondition();
        });
        btnRefund.setOnClickListener(v -> {
            showRefundDialog();
        });
    }

    private void showRefundDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.coin_take, null);
        Dialog dialog = new Dialog(getActivity());
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            Thread t = new Thread(() -> {
                ApacheServerRequest.moneyRefundStart(refund5.getText().toString(), refund10.getText().toString(), refund50.getText().toString());
            });
            try {
                t.start();
                t.join();
                setMoneyCondition();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showSupplyChooseDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.supply_choose, null);
        Dialog dialog = new Dialog(getActivity());
        ToggleButton btnPour = dialogView.findViewById(R.id.toggleButton_pour);
//        ToggleButton btnInsert = dialogView.findViewById(R.id.toggleButton_insert);
        btnPour.setChecked(true);
//        btnPour.setOnClickListener(v -> {
//            if (btnInsert.isChecked()) {
//                btnInsert.setChecked(false);
//            } else {
//                btnPour.setChecked(true);
//            }
//        });
//        btnInsert.setOnClickListener(v -> {
//            if (btnPour.isChecked()) {
//                btnPour.setChecked(false);
//            } else {
//                btnInsert.setChecked(true);
//            }
//        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            MoneyCount moneyCount = getMoneyCount();
            if (moneyCount != null) {
                int fiveInput = Integer.parseInt(input5.getText().toString());
                int tenInput = Integer.parseInt(input10.getText().toString());
                int fiftyInput = Integer.parseInt(input50.getText().toString());
                if (btnPour.isChecked()) {
                    Thread t = new Thread(() -> {
                        ApacheServerRequest.moneyCountUpdate(
                                moneyCount.getFive() + fiveInput, moneyCount.getTen() + tenInput, moneyCount.getFifty() + fiftyInput, moneyCount.getHundred());
                    });
                    try {
                        t.start();
                        t.join();
                        setMoneyCondition();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
//                    showCoinInsertDialog(input5.getText().toString(), input10.getText().toString(), input50.getText().toString());
                }
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showCoinInsertDialog(String input5, String input10, String input50) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.moneySupplyStart(input5, input10, input50);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final View dialogView = View.inflate(getActivity(), R.layout.coin_insert, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtTotal5 = dialogView.findViewById(R.id.total_5);
        TextView txtTotal10 = dialogView.findViewById(R.id.total_10);
        TextView txtTotal50 = dialogView.findViewById(R.id.total_50);
        TextView txtInput5 = dialogView.findViewById(R.id.input_5);
        TextView txtInput10 = dialogView.findViewById(R.id.input_10);
        TextView txtInput50 = dialogView.findViewById(R.id.input_50);
        txtTotal5.setText(input5);
        txtTotal10.setText(input10);
        txtTotal50.setText(input50);
        dialog.setContentView(dialogView);
        dialog.show();
        new Thread(() -> getActivity().runOnUiThread(() -> {
            boolean start = true;
            while (start) {
                try {
                    Thread.sleep(1000);
                    MoneySupply supply = getMoneySupply();
                    if (supply.getSupply() == 0) {
                        start = false;
                    } else {
                        txtInput5.setText(String.valueOf(supply.getFive_count()));
                        txtInput10.setText(String.valueOf(supply.getTen_count()));
                        txtInput50.setText(String.valueOf(supply.getFifty_count()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dialog.dismiss();
            setMoneyCondition();
        })).start();

    }

    private void moneyBasicSetting() {
        basic5 = binding.editTextFiveBasicSetting;
        basic10 = binding.editTextTenBasicSetting;
        basic50 = binding.editTextFiftyBasicSetting;
        reminder5 = binding.reminder5;
        reminder10 = binding.reminder10;
        reminder50 = binding.reminder50;
        btnBasicSetting = binding.buttonSetting;
        setMoneyBasic();
        btnBasicSetting.setOnClickListener(v -> {
            Thread t = new Thread(() -> {
                ApacheServerRequest.moneyBasicUpdate(basic5.getText().toString(), basic10.getText().toString(), basic50.getText().toString(),
                        reminder5.getText().toString(), reminder10.getText().toString(), reminder50.getText().toString());
            });
            try {
                t.start();
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private MoneySupply getMoneySupply() {
        Var<MoneySupply> moneySupply = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerRequest.moneySupplySearch();
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        MoneySupply supply = gson.fromJson(obj.toString(), MoneySupply.class);
                        moneySupply.set(supply);
                    }
                }
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moneySupply.get();
    }

    private MoneyBasic getMoneyBasic() {
        Var<MoneyBasic> moneyBasic = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerRequest.moneyBasicSearch();
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        MoneyBasic basic = gson.fromJson(obj.toString(), MoneyBasic.class);
                        moneyBasic.set(basic);
                    }
                }
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moneyBasic.get();
    }

    private void setMoneyBasic() {
        MoneyBasic moneyBasic = getMoneyBasic();
        if (moneyBasic != null) {
            basic5.setText(String.valueOf(moneyBasic.getFive_basic()));
            basic10.setText(String.valueOf(moneyBasic.getTen_basic()));
            basic50.setText(String.valueOf(moneyBasic.getFifty_basic()));
            reminder5.setText(String.valueOf(moneyBasic.getFive_alert()));
            reminder10.setText(String.valueOf(moneyBasic.getTen_alert()));
            reminder50.setText(String.valueOf(moneyBasic.getFifty_alert()));
        }
    }

    private void moneyConditionSetting() {
        amount5 = binding.amount5;
        amount10 = binding.amount10;
        amount50 = binding.amount50;
        amount100 = binding.amount100;
        count5 = binding.count5;
        count10 = binding.count10;
        count50 = binding.count50;
        count100 = binding.count100;
        countTotal = binding.countTotal;
        setMoneyCondition();
        buttonTakeMoney = binding.buttonTakeMoney;
        buttonTakeMoney.setOnClickListener(v -> {
            showBillTakeDialog();
        });
    }

    private void showBillTakeDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.bill_take, null);
        Dialog dialog = new Dialog(getActivity());
        ToggleButton isBillTaken = dialogView.findViewById(R.id.toggleButton);
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            MoneyCount moneyCount = getMoneyCount();
            MoneyBasic moneyBasic = getMoneyBasic();
            if (moneyCount != null && moneyBasic != null) {
                Thread t = new Thread(() -> {
                    if (isBillTaken.isChecked()) {
                        ApacheServerRequest.moneyCountUpdate(moneyCount.getFive(), moneyCount.getTen(), moneyCount.getFifty(), 0);
                    }
                    int refundFive = moneyCount.getFive() - moneyBasic.getFive_basic();
                    int refundTen = moneyCount.getTen() - moneyBasic.getTen_basic();
                    int refundFifty = moneyCount.getFifty() - moneyBasic.getFifty_basic();
                    if (refundFive < 0) {
                        refundFive = 0;
                    }
                    if (refundTen < 0) {
                        refundTen = 0;
                    }
                    if (refundFifty < 0) {
                        refundFifty = 0;
                    }
                    ApacheServerRequest.moneyRefundStart(String.valueOf(refundFive), String.valueOf(refundTen), String.valueOf(refundFifty));
                });
                try {
                    t.start();
                    t.join();
                    setMoneyCondition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private MoneyCount getMoneyCount() {
        Var<MoneyCount> moneyCount = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerRequest.moneyCountSearch();
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        MoneyCount count = gson.fromJson(obj.toString(), MoneyCount.class);
                        moneyCount.set(count);
                    }
                }
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moneyCount.get();
    }

    private void setMoneyCondition() {
        MoneyCount moneyCount = getMoneyCount();
        if (moneyCount != null) {
            amount5.setText(String.valueOf(moneyCount.getFive()));
            amount10.setText(String.valueOf(moneyCount.getTen()));
            amount50.setText(String.valueOf(moneyCount.getFifty()));
            amount100.setText(String.valueOf(moneyCount.getHundred()));
            count5.setText(String.valueOf(moneyCount.getFive() * 5));
            count10.setText(String.valueOf(moneyCount.getTen() * 10));
            count50.setText(String.valueOf(moneyCount.getFifty() * 50));
            count100.setText(String.valueOf(moneyCount.getHundred() * 100));
            countTotal.setText(String.valueOf(moneyCount.getFive() * 5 + moneyCount.getTen() * 10 + moneyCount.getFifty() * 50 + moneyCount.getHundred() * 100));
        }
    }

    private void revenueSetting() {
        txtCashDay = binding.cashToday;
        txtCashMonth = binding.cashMonth;
        txtECPayDay = binding.ecpayToday;
        txtECPayMonth = binding.ecpayMonth;
        txtDay = binding.todayCount;
        txtMonth = binding.monthCount;
        setRevenueDay();
        setRevenueMonth();
    }

    private void setRevenueDay() {
        Var<Integer> cash = new Var<>(0);
        Var<Integer> ec = new Var<>(0);
        Thread t = new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date start = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date end = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            String startDate = formatter.format(start);
            String endDate = formatter.format(end);
            try {
                String res = ApacheServerRequest.getPayHistoryWithDates(startDate, endDate, "", "");
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject pay = array.getJSONObject(i);
                        if (pay.has("cost") && pay.has("payment")) {
                            int money = pay.getInt("cost");
                            String payment = pay.getString("payment");
                            if ("C".equals(payment)) {
                                cash.set(cash.get() + money);
                            } else {
                                ec.set(ec.get() + money);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("getLeftLots", "getLeftLots");
            }
        });
        try {
            t.start();
            t.join();
            txtCashDay.setText(String.valueOf(cash.get()));
            txtECPayDay.setText(String.valueOf(ec.get()));
            txtDay.setText(String.valueOf(cash.get() + ec.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRevenueMonth() {
        Var<Integer> cash = new Var<>(0);
        Var<Integer> ec = new Var<>(0);
        Thread t = new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date start = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date end = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            String startDate = formatter.format(start);
            String endDate = formatter.format(end);
            try {
                String res = ApacheServerRequest.getPayHistoryWithDates(startDate, endDate, "", "");
                if (!res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject pay = array.getJSONObject(i);
                        if (pay.has("cost") && pay.has("payment")) {
                            int money = pay.getInt("cost");
                            String payment = pay.getString("payment");
                            if ("C".equals(payment)) {
                                cash.set(cash.get() + money);
                            } else {
                                ec.set(ec.get() + money);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
            txtCashMonth.setText(String.valueOf(cash.get()));
            txtECPayMonth.setText(String.valueOf(ec.get()));
            txtMonth.setText(String.valueOf(cash.get() + ec.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshRevenue() {
        new Thread(() -> {
            while(getActivity() != null){
                getActivity().runOnUiThread(()->{
                    setRevenueDay();
                    setRevenueMonth();
                    setMoneyCondition();
                });
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RevenueManageViewModel.class);
        // TODO: Use the ViewModel
    }

}