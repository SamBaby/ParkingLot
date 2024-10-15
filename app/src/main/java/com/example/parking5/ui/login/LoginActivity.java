package com.example.parking5.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.MainActivity;
import com.example.parking5.R;
import com.example.parking5.data.LoginRepository;
import com.example.parking5.database.MyDatabaseHelper;
import com.example.parking5.databinding.ActivityLoginBinding;
import com.example.parking5.datamodel.PreferenceIP;
import com.example.parking5.datamodel.User;
import com.example.parking5.event.Var;
import com.example.parking5.util.IP;

import java.util.Vector;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    EditText ipEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        ipEditText = binding.ip;
        usernameEditText.setText(getPreferenceAccount());
        ipEditText.setText(getPreferenceIP());
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        ipEditText.setInputType(InputType.TYPE_NULL);
        ipEditText.setOnClickListener(v -> {
            showIPList();
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
//                setResult(Activity.RESULT_OK);
                User user = LoginRepository.getInstance().getLoggedInUser();
                if (user != null) {
                    setPreferenceAccount(user.getAccount());
                }
                setPreferenceIP(IP.getInstance().getIp());
                //Complete and destroy login activity once successful
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), ipEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), ipEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showIPList() {
        final View dialogView = View.inflate(this, R.layout.account_preference_list, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        Var<TableLayout> table = new Var<>(dialogView.findViewById(R.id.table_ip_list));
        Var<TableRow> selectedRow = new Var<>(null);
        Var<Vector<PreferenceIP>> list = new Var<>(new Vector<>());
        tableRefresh(table.get(), selectedRow, list);
        AlertDialog dialog = dialogBuilder.create();
        dialogView.findViewById(R.id.add_button).setOnClickListener((v) -> {
            tableAdd(table.get(), selectedRow, list);
        });
        dialogView.findViewById(R.id.delete_button).setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                int index = table.get().indexOfChild(selectedRow.get());
                deleteIPList(list.get().get(index).getIp());
                tableRefresh(table.get(), selectedRow, list);
            }
        });
        dialogView.findViewById(R.id.modify_button).setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                tableModify(table.get(), selectedRow, list);
            }
        });
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                int index = table.get().indexOfChild(selectedRow.get());
                ipEditText.setText(list.get().get(index).getIp());
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.show();
    }

    private void tableAdd(TableLayout table, Var<TableRow> selectedRow, Var<Vector<PreferenceIP>> list) {
        final View dialogView = View.inflate(this, R.layout.account_preference_add, null);
        Dialog dialog = new Dialog(this);
        EditText textIP = dialogView.findViewById(R.id.ip_edittext);
        Switch switchRouter = dialogView.findViewById(R.id.switch_router);

        dialogView.findViewById(R.id.confirm_button).setOnClickListener(view -> {
            String ip = textIP.getText().toString();
            int router = switchRouter.isChecked() ? 1 : 0;
            if (!ip.isEmpty()) {
                addIPList(ip, router);
                tableRefresh(table, selectedRow, list);
            }
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(view -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void tableModify(TableLayout table, Var<TableRow> selectedRow, Var<Vector<PreferenceIP>> list) {
        final View dialogView = View.inflate(this, R.layout.account_preference_add, null);
        Dialog dialog = new Dialog(this);
        EditText textIP = dialogView.findViewById(R.id.ip_edittext);
        Switch switchRouter = dialogView.findViewById(R.id.switch_router);
        int index = table.indexOfChild(selectedRow.get());
        PreferenceIP preferenceIP = list.get().get(index);
        textIP.setText(preferenceIP.getIp());
        switchRouter.setChecked(preferenceIP.getVpn() == 1);
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(view -> {
            String ip = textIP.getText().toString();
            int router = switchRouter.isChecked() ? 1 : 0;
            if (!ip.isEmpty()) {
                modifyIPList(preferenceIP.getIp(), ip, router);
                tableRefresh(table, selectedRow, list);
            }
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(view -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void tableRefresh(TableLayout table, Var<TableRow> selectedRow, Var<Vector<PreferenceIP>> list) {
        list.get().clear();
        list.set(getIPList());
        table.removeAllViews();
        selectedRow.set(null);
        final int[] tableWeight = {3, 1};
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < list.get().size(); i++) {
            TableRow tableRow = new TableRow(table.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            PreferenceIP ip = list.get().get(i);
            // 为每行添加单元格
            for (int j = 0; j < 2; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textView.setText(ip.getIp());
                        break;
                    case 1:
                        textView.setText(ip.getVpn() == 0 ? "否" : "是");
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
            // 将 TableRow 添加到 TableLayout
            table.addView(tableRow);
        }
    }

    private Vector<PreferenceIP> getIPList() {
        Vector<PreferenceIP> list = new Vector<>();
        try {

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns you want to query
            String[] projection = {"id", "ip", "vpn"};

// Query the database
            Cursor cursor = db.query("ip_preference",   // The table to query
                    projection,            // The columns to return
                    null,                  // The columns for the WHERE clause
                    null,                  // The values for the WHERE clause
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null                   // The sort order
            );

// Iterate over the cursor
            while (cursor.moveToNext()) {
                String ip = cursor.getString(cursor.getColumnIndexOrThrow("ip"));
                int vpn = cursor.getInt(cursor.getColumnIndexOrThrow("vpn"));
                PreferenceIP preferenceIP = new PreferenceIP(ip, vpn);
                list.add(preferenceIP);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void addIPList(String ip, int router) {

        try {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("ip", ip);
            values.put("vpn", router);

            long newRowId = db.insert("ip_preference", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyIPList(String oldIP, String ip, int router) {

        try {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("ip", ip);
            values.put("vpn", router);

// Update the row where id equals 1
            String selection = "ip = ?";
            String[] selectionArgs = {oldIP};

            int count = db.update("ip_preference", values, selection, selectionArgs);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteIPList(String ip) {
        try {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

// Define 'where' part of query.
            String selection = "ip = ?";
// Specify arguments in placeholder order.
            String[] selectionArgs = {ip};
// Issue SQL statement.
            db.delete("ip_preference", selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPreferenceIP() {
        // 獲取 SharedPreferences 的實例
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // 讀取數據
        return sharedPreferences.getString("ip", "");
    }

    private String getPreferenceAccount() {
        // 獲取 SharedPreferences 的實例
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // 讀取數據
        return sharedPreferences.getString("account", "");
    }

    private void setPreferenceIP(String ip) {
        // 獲取 SharedPreferences 的實例
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // 編輯 SharedPreferences 並存儲資料
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ip", ip);
        // 提交改變
        editor.apply(); // 使用 apply() 保存更改
    }

    private void setPreferenceAccount(String account) {
        // 獲取 SharedPreferences 的實例
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // 編輯 SharedPreferences 並存儲資料
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);

        // 提交改變
        editor.apply(); // 使用 apply() 保存更改
    }
}