package com.example.parking5.ui.regular;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
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
import com.example.parking5.databinding.FragmentRegularBinding;
import com.example.parking5.datamodel.RegularPass;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class RegularFragment extends Fragment {

    private RegularViewModel mViewModel;
    private FragmentRegularBinding binding;
    private static final int[] tableWeight = new int[]{1, 1, 2, 2, 2};
    Var<TableRow> selectedRow;
    private Vector<RegularPass> passes;

    public static RegularFragment newInstance() {
        return new RegularFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegularBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedRow = new Var<>();
        passes = new Vector<>();
        tableSetting();

        Button btnAdd = binding.buttonAccountSettingAdd;
        Button btnModify = binding.buttonAccountSettingModify;
        Button btnDelete = binding.buttonAccountSettingDelete;
        btnAdd.setOnClickListener((v) -> {
            showAccountAddDialog();
        });
        btnModify.setOnClickListener((v) -> {
            showAccountUpdateDialog();
        });
        btnDelete.setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                TableLayout tableUser = binding.tableUser;
                int index = tableUser.indexOfChild(selectedRow.get());
                if (passes.size() > index) {
                    deleteAccount(passes.get(index).getId());
                    tableSetting();
                    selectedRow.set(null);
                }
            }

        });
        return root;
    }

    private void tableSetting() {
        TableLayout tableUser = binding.tableUser;
        tableUser.removeAllViews();
        selectedRow.set(null);
        getUsers();
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < passes.size(); i++) {
            TableRow tableRow = new TableRow(tableUser.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            // 将 TableRow 添加到 TableLayout
            tableUser.addView(tableRow);

            RegularPass pass = passes.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 5; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                switch (j) {
                    case 0:
                        textView.setText(pass.getCar_number());
                        break;
                    case 1:
                        textView.setText(pass.getCustomer_name());
                        break;
                    case 2:
                        textView.setText(pass.getStart_date());
                        break;
                    case 3:
                        textView.setText(pass.getDue_date());
                        break;
                    case 4:
                        textView.setText(pass.getPhone_number());
                        break;
                    default:
                        break;
                }
                tableRow.addView(textView, layoutParams);
                tableRow.setOnClickListener(v -> {
                    if (selectedRow.get() != null) {
                        selectedRow.get().setBackground(null);
                    }
                    v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.border));
                    selectedRow.set((TableRow) v);
                });
            }

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegularViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showAccountAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.regular_add, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtStart = dialogView.findViewById(R.id.start_time_editText);
        TextView txtEnd = dialogView.findViewById(R.id.end_time_editText);
        txtStart.setOnClickListener(v -> {
            Util.showDateDialog(getActivity(), txtStart);
        });
        txtEnd.setOnClickListener(v -> {
            Util.showDateDialog(getActivity(), txtEnd);
        });
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String carNumber = String.valueOf(((TextView) dialogView.findViewById(R.id.car_number_edittext)).getText());
            String name = String.valueOf(((TextView) dialogView.findViewById(R.id.name_edittext)).getText());
            String phone = String.valueOf(((TextView) dialogView.findViewById(R.id.phone_number_editText)).getText());
            String start = String.valueOf(txtStart.getText());
            String end = String.valueOf(txtEnd.getText());
            if (!carNumber.isEmpty() && !name.isEmpty() && !phone.isEmpty() && !start.isEmpty() && !end.isEmpty()) {
                addAccount(carNumber, name, phone, start, end);
                tableSetting();
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showAccountUpdateDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.regular_add, null);
        Dialog dialog = new Dialog(getActivity());
        TableLayout tableUser = binding.tableUser;
        int index = tableUser.indexOfChild(selectedRow.get());
        RegularPass pass = passes.get(index);

        TextView title = dialogView.findViewById(R.id.textView_title);
        title.setText(getString(R.string.regular_modify));
        TextView txtCar = dialogView.findViewById(R.id.car_number_edittext);
        TextView txtName = dialogView.findViewById(R.id.name_edittext);
        TextView txtPhone = dialogView.findViewById(R.id.phone_number_editText);
        TextView txtStart = dialogView.findViewById(R.id.start_time_editText);
        TextView txtEnd = dialogView.findViewById(R.id.end_time_editText);
        txtStart.setOnClickListener(v -> {
            Util.showDateDialog(getActivity(), txtStart);
        });
        txtEnd.setOnClickListener(v -> {
            Util.showDateDialog(getActivity(), txtEnd);
        });
        txtCar.setText(pass.getCar_number());
        txtName.setText(pass.getCustomer_name());
        txtPhone.setText(pass.getPhone_number());
        txtStart.setText(pass.getStart_date());
        txtEnd.setText(pass.getDue_date());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String carNumber = String.valueOf(txtCar.getText());
            String name = String.valueOf(txtName.getText());
            String phone = String.valueOf(txtPhone.getText());
            String start = String.valueOf(txtStart.getText());
            String end = String.valueOf(txtEnd.getText());
            if (!carNumber.isEmpty() && !name.isEmpty() && !phone.isEmpty() && !start.isEmpty() && !end.isEmpty()) {
                updateAccount(pass.getId(), carNumber, name, phone, start, end);
                tableSetting();
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void addAccount(String account, String password, String name, String phone, String permission) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.regularPassAdd(account, password, name, phone, permission);
        });
        try {
            t.start();
            t.join();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAccount(int id, String account, String password, String name, String phone, String permission) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.regularPassUpdate(id, account, password, name, phone, permission);
        });
        try {
            t.start();
            t.join();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAccount(int id) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.regularPassDelete(id);
        });
        try {
            t.start();
            t.join();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUsers() {
        passes.clear();
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.regularPassSearch();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        RegularPass pass = gson.fromJson(obj.toString(), RegularPass.class);
                        passes.add(pass);
                    }
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
}