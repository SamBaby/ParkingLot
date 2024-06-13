package com.example.parking5.ui.data_search;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.MainActivity;
import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryOutBinding;
import com.example.parking5.datamodel.CarInside;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public class HistoryOutFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 1, 1, 1};
    private HistoryOutViewModel mViewModel;
    private FragmentHistoryOutBinding binding;

    public static HistoryOutFragment newInstance() {
        return new HistoryOutFragment();
    }

    private Vector<CarInside> cars;
    Var<TableRow> selectedRow = new Var<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryOutViewModel.class);

        binding = FragmentHistoryOutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cars = new Vector<>();

        tableSetting();
        buttonSetting();
        return root;
    }

    private void buttonSetting() {
        Button btnAdd = binding.buttonAdd;
        Button btnDelete = binding.buttonDelete;
        Button btnSearch = binding.buttonSearch;
        btnAdd.setOnClickListener(v -> {
            showAddDialog();
        });
        btnDelete.setOnClickListener(v -> {
            deleteCarInside();
        });
        btnSearch.setOnClickListener(v -> {
            showSearchDialog();
        });
    }

    private void deleteCarInside() {
        if (selectedRow.get() != null) {
            TableLayout table = binding.tableOutData;
            int index = table.indexOfChild(selectedRow.get());
            deleteCarInsideData(cars.get(index).getCar_number());
            tableSetting();
        }
    }

    private void showAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.allow_exit_add, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtCarNumber = dialogView.findViewById(R.id.car_number_edittext);
        TextView txtExitTime = dialogView.findViewById(R.id.textView_exit_time);
        txtExitTime.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtExitTime);
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            if (!txtCarNumber.getText().toString().isEmpty() && !txtExitTime.getText().toString().isEmpty()) {
                String number = txtCarNumber.getText().toString();
                String time = txtExitTime.getText().toString();
                boolean isCarInside = isCarInside(number);
                if (!isCarInside) {
                    addCarInside(number);
                }
                addCarInsidePay(number, time);
                tableSetting();
                dialog.dismiss();
            }

        });
        dialog.setContentView(dialogView);
        dialog.show();

    }

    private void showSearchDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.allow_exit_search, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtCar = dialogView.findViewById(R.id.car_number_edittext);
        ToggleButton buttonNonRegular = dialogView.findViewById(R.id.toggleButton_non_regular);
        ToggleButton buttonRegular = dialogView.findViewById(R.id.toggleButton_regular);
        ToggleButton buttonAll = dialogView.findViewById(R.id.toggleButton_all);
        buttonAll.setChecked(true);
        buttonNonRegular.setOnClickListener(v -> {
            buttonRegular.setChecked(false);
            buttonAll.setChecked(false);
        });
        buttonRegular.setOnClickListener(v -> {
            buttonNonRegular.setChecked(false);
            buttonAll.setChecked(false);
        });
        buttonAll.setOnClickListener(v -> {
            buttonNonRegular.setChecked(false);
            buttonRegular.setChecked(false);
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            int type = 0;
            if (buttonNonRegular.isChecked()) {
                type = 1;
            } else if (buttonRegular.isChecked()) {
                type = 2;
            }
            tableSetting(txtCar.getText().toString(), type);
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void tableSetting() {
        tableSetting("", 0);
    }

    private void tableSetting(String carNumber, int type) {
        TableLayout tableData = binding.tableOutData;
        tableData.removeAllViews();
        selectedRow.set(null);
        getCarsWithNumber(carNumber, type);
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < cars.size(); i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            CarInside car = cars.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 4; j++) {
                TextView textView = new TextView(tableRow.getContext());
                textView.setPadding(5, 5, 5, 5);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textView.setText(car.getCar_number());
                        break;
                    case 1:
                        textView.setText(car.getTime_in());
                        break;
                    case 2:
                        textView.setText(car.getTime_pay());
                        break;
                    case 3:
                        textView.setText("臨停");
                        break;
                    default:
                        break;
                }
                tableRow.addView(textView);
            }
            tableRow.setOnLongClickListener(v -> {
                // Load the image and set it to the ImageView
                Bitmap bitmap = BitmapFactory.decodeFile(car.getPicture_url());
                showImageDialog(bitmap);
                return false;
            });
            tableRow.setOnClickListener(v -> {
                if (selectedRow.get() != null) {
                    selectedRow.get().setBackground(null);
                }
                v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.border));
                selectedRow.set((TableRow) v);
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

    private void getCarsWithNumber(String number, int type) {
        Thread t = new Thread(() -> {
            String json = "";
            if (!number.isEmpty()) {
                json = ApacheServerReqeust.getCarInsideWithCarNumber(number);
            } else {
                json = ApacheServerReqeust.getCarInside();
            }

            try {
                JSONArray array = new JSONArray(json);
                cars.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    CarInside car = gson.fromJson(obj.toString(), CarInside.class);
                    if (car.getTime_pay() != null && !car.getTime_pay().isEmpty()) {
                        if (type == 0) {
                            cars.add(car);
                        } else {
                            int carType = getCarType(car);
                            if (carType == type) {
                                cars.add(car);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCarType(CarInside car) {
        int ret = 1;
        return ret;
    }

    private boolean isCarInside(String number) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            String res = ApacheServerReqeust.getCarInsideWithCarNumber(number);
            try {
                JSONArray array = new JSONArray(res);
                if (array.length() > 0) {
                    ret.set(true);
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
        return ret.get();
    }

    private boolean addCarInside(String number) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            Calendar c = new GregorianCalendar();
            String start = formatter.format(c.getTime());
            ApacheServerReqeust.addCarInsideWithCarNumber(number, start);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }

    private boolean addCarInsidePay(String number, String payTime) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            ApacheServerReqeust.setCarInsidePay(number, payTime, 0,
                    0, "", "A");
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }

    private void deleteCarInsideData(String carNumber) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.deleteCarInside(carNumber);
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
        mViewModel = new ViewModelProvider(this).get(HistoryOutViewModel.class);
        // TODO: Use the ViewModel
    }

}