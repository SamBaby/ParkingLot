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
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Vector;

public class HistoryOutFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 1, 1, 1};
    private HistoryOutViewModel mViewModel;
    private FragmentHistoryOutBinding binding;

    public static HistoryOutFragment newInstance() {
        return new HistoryOutFragment();
    }

    private Vector<CarInside> cars;

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

        });
        btnSearch.setOnClickListener(v -> {
            showSearchDialog();
        });
    }

    private void showAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.allow_exit_add, null);
        Dialog dialog = new Dialog(getActivity());

        TextView txtExitTime = dialogView.findViewById(R.id.textView_exit_time);
        txtExitTime.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtExitTime);
        });
//        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();

    }

    private void showSearchDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.allow_exit_search, null);
        Dialog dialog = new Dialog(getActivity());
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
    }

    private void tableSetting() {
        tableSetting("", "");
    }

    private void tableSetting(String start, String end) {
        TableLayout tableData = binding.tableOutData;
        tableData.removeAllViews();
        getCarsWithDates(start, end);
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < cars.size(); i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
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

    private void getCarsWithDates(String start, String end) {
        Thread t = new Thread(() -> {
            String json = "";
            if (!start.isEmpty()) {
                json = ApacheServerReqeust.getCarInsideWithDates(start, end);
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
                    cars.add(car);
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
        mViewModel = new ViewModelProvider(this).get(HistoryOutViewModel.class);
        // TODO: Use the ViewModel
    }

}