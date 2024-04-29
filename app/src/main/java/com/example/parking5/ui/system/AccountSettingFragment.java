package com.example.parking5.ui.system;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentAccountSettingBinding;
import com.example.parking5.databinding.FragmentHomeBinding;
import com.example.parking5.dialog.AccountAddDialog;
import com.example.parking5.event.Var;
import com.example.parking5.ui.home.HomeViewModel;
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class AccountSettingFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 2, 2, 4, 1};
    Var<TableRow> selectedRow;
    private AccountSettingViewModel mViewModel;
    private FragmentAccountSettingBinding binding;

    public static AccountSettingFragment newInstance() {
        return new AccountSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(AccountSettingViewModel.class);

        binding = FragmentAccountSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedRow = new Var<>();
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
                String account = (String) ((TextView) selectedRow.get().getChildAt(0)).getText();
                deleteAccount(account);
            }

        });
        return root;
    }

    private void tableSetting() {
        TableLayout tableUser = binding.tableUser;
        tableUser.removeAllViews();
        Vector<User> users = mViewModel.getUsers().getValue();
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < users.size(); i++) {
            TableRow tableRow = new TableRow(tableUser.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            User user = users.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 5; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textView.setText(user.getAccount());
                        break;
                    case 1:
                        textView.setText(user.getPassword());
                        break;
                    case 2:
                        textView.setText(user.getName());
                        break;
                    case 3:
                        textView.setText(user.getPhone());
                        break;
                    case 4:
                        textView.setText(user.getPermission());
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
            tableUser.addView(tableRow);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AccountSettingViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showAccountAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.account_add, null);
        Dialog dialog = new Dialog(getActivity());
        ToggleButton buttonA = dialogView.findViewById(R.id.toggleButton_A);
        ToggleButton buttonB = dialogView.findViewById(R.id.toggleButton_B);
        ToggleButton buttonC = dialogView.findViewById(R.id.toggleButton_C);
        ToggleButton buttonD = dialogView.findViewById(R.id.toggleButton_D);
        buttonD.setChecked(true);
        buttonA.setOnClickListener((v) -> {
            buttonB.setChecked(false);
            buttonC.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonB.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonC.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonC.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonB.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonD.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonB.setChecked(false);
            buttonC.setChecked(false);
        });
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String account = String.valueOf(((TextView) dialogView.findViewById(R.id.account_edittext)).getText());
            String password = String.valueOf(((TextView) dialogView.findViewById(R.id.password_editText)).getText());
            String name = String.valueOf(((TextView) dialogView.findViewById(R.id.name_edittext)).getText());
            String phone = String.valueOf(((TextView) dialogView.findViewById(R.id.phone_editText)).getText());
            String permission = "";
            if (buttonA.isChecked()) {
                permission = "A";
            } else if (buttonB.isChecked()) {
                permission = "B";
            } else if (buttonC.isChecked()) {
                permission = "C";
            } else if (buttonD.isChecked()) {
                permission = "D";
            }
            if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phone.isEmpty() && !permission.isEmpty()) {
                addAccount(account, password, name, phone, permission);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showAccountUpdateDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.account_add, null);
        Dialog dialog = new Dialog(getActivity());
        TextView title = dialogView.findViewById(R.id.textView_title);
        title.setText(getString(R.string.account_update));

        EditText txtAccount = dialogView.findViewById(R.id.account_edittext);
        EditText txtPassword = dialogView.findViewById(R.id.password_editText);
        EditText txtName = dialogView.findViewById(R.id.name_edittext);
        EditText txtPhone = dialogView.findViewById(R.id.phone_editText);
        txtAccount.setText((String) ((TextView) selectedRow.get().getChildAt(0)).getText());
        txtPassword.setText((String) ((TextView) selectedRow.get().getChildAt(1)).getText());
        txtName.setText((String) ((TextView) selectedRow.get().getChildAt(2)).getText());
        txtPhone.setText((String) ((TextView) selectedRow.get().getChildAt(3)).getText());
        ToggleButton buttonA = dialogView.findViewById(R.id.toggleButton_A);
        ToggleButton buttonB = dialogView.findViewById(R.id.toggleButton_B);
        ToggleButton buttonC = dialogView.findViewById(R.id.toggleButton_C);
        ToggleButton buttonD = dialogView.findViewById(R.id.toggleButton_D);
        String perm = (String) ((TextView) selectedRow.get().getChildAt(4)).getText();
        switch (perm) {
            case "A":
                buttonA.setChecked(true);
                break;
            case "B":
                buttonB.setChecked(true);
                break;
            case "C":
                buttonC.setChecked(true);
                break;
            case "D":
                buttonD.setChecked(true);
                break;
            default:
                break;
        }
        buttonA.setOnClickListener((v) -> {
            buttonB.setChecked(false);
            buttonC.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonB.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonC.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonC.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonB.setChecked(false);
            buttonD.setChecked(false);
        });
        buttonD.setOnClickListener((v) -> {
            buttonA.setChecked(false);
            buttonB.setChecked(false);
            buttonC.setChecked(false);
        });
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String account = String.valueOf(((TextView) dialogView.findViewById(R.id.account_edittext)).getText());
            String password = String.valueOf(((TextView) dialogView.findViewById(R.id.password_editText)).getText());
            String name = String.valueOf(((TextView) dialogView.findViewById(R.id.name_edittext)).getText());
            String phone = String.valueOf(((TextView) dialogView.findViewById(R.id.phone_editText)).getText());
            String permission = "";
            if (buttonA.isChecked()) {
                permission = "A";
            } else if (buttonB.isChecked()) {
                permission = "B";
            } else if (buttonC.isChecked()) {
                permission = "C";
            } else if (buttonD.isChecked()) {
                permission = "D";
            }
            if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phone.isEmpty() && !permission.isEmpty()) {
                updateAccount(account, password, name, phone, permission);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void addAccount(String account, String password, String name, String phone, String permission) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.addUser(account, password, name, phone, permission);
        });
        try {
            t.start();
            t.join();
            mViewModel.refreshUsers();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAccount(String account, String password, String name, String phone, String permission) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.updateUser(account, password, name, phone, permission);
        });
        try {
            t.start();
            t.join();
            mViewModel.refreshUsers();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAccount(String account) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.deleteUser(account);
        });
        try {
            t.start();
            t.join();
            mViewModel.refreshUsers();
            tableSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}