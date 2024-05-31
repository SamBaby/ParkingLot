package com.example.parking5.ui.data_search;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int[] tableWeight = new int[]{2, 1, 1, 1, 2};
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
        return root;
    }
    private void tableSetting() {
        tableSetting("", "");
    }

    private void tableSetting(String start, String end) {
        TableLayout tableData = binding.tableOutData;
        tableData.removeAllViews();
        new Thread(() -> {
            getCarsWithDates(start, end);
            getActivity().runOnUiThread(() -> {
                // 遍历数据列表并为每行创建 TableRow
                for (int i = 0; i < cars.size(); i++) {
                    TableRow tableRow = new TableRow(tableData.getContext());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    CarInside car = cars.get(i);
                    // 为每行添加单元格
                    for (int j = 0; j < 5; j++) {
                        if (j < 4) {
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
                        } else {
                            ImageView imageView = new ImageView(tableRow.getContext());
                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                            imageView.setLayoutParams(layoutParams);
                            try {
                                // Load the image and set it to the ImageView
                                Bitmap bitmap = BitmapFactory.decodeFile(car.getPicture_url());
                                // Handle the case where the image could not be loaded
                                imageView.setImageBitmap(bitmap);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            tableRow.addView(imageView);
                        }
                    }
                    // 将 TableRow 添加到 TableLayout
                    tableData.addView(tableRow);
                }
            });
        }).start();
    }

    private void getCarsWithDates(String start, String end) {
        try {
            String json = "";
            if (!start.isEmpty()) {
                json = ApacheServerReqeust.getCarInsideWithDates(start, end);
            } else {
                json = ApacheServerReqeust.getCarInside();
            }

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryOutViewModel.class);
        // TODO: Use the ViewModel
    }

}