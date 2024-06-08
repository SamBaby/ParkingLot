package com.example.parking5.ui.data_search;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentHistoryLicenseBinding;
import com.example.parking5.datamodel.CarInside;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Vector;

public class HistoryLicenseFragment extends Fragment {
    private static final int[] tableWeight = new int[]{2, 3, 1, 1, 1};
    private HistoryLicenseViewModel mViewModel;
    private FragmentHistoryLicenseBinding binding;
    private Vector<CarInside> cars;
    private Var<TableRow> selectedRow;

    public static HistoryLicenseFragment newInstance() {
        return new HistoryLicenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryLicenseViewModel.class);
        binding = FragmentHistoryLicenseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cars = new Vector<>();
        selectedRow = new Var<>();
        tableSetting();
        buttonSetting();
        return root;
    }
    private void buttonSetting() {
        Button btnModify = binding.buttonModify;
        Button btnDelete = binding.buttonDelete;
        Button btnSearch = binding.buttonSearch;
        btnModify.setOnClickListener(v -> {
        });
        btnDelete.setOnClickListener(v -> {
        });
        btnSearch.setOnClickListener(v -> {
        });
    }
    private void tableSetting() {
        tableSetting("", "");
    }

    private void tableSetting(String start, String end) {
        TableLayout tableData = binding.tableCarData;
        tableData.removeAllViews();
        getCarsWithDates(start, end);
        // 遍历数据列表并为每行创建 TableRow
        for (int i = 0; i < cars.size(); i++) {
            TableRow tableRow = new TableRow(tableData.getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            CarInside car = cars.get(i);
            // 为每行添加单元格
            for (int j = 0; j < 5; j++) {
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
                        textView.setText("小型");
                        break;
                    case 3:
                        textView.setText("白");
                        break;
                    case 4:
                        textView.setText(car.getArtificial() == 0 ? "否" : "是");
                        break;
                    default:
                        break;
                }
                tableRow.addView(textView);
            }
            // 将 TableRow 添加到 TableLayout
            tableRow.setOnClickListener(v -> {
                if (selectedRow.get() != null) {
                    selectedRow.get().setBackground(null);
                }
                v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.border));
                selectedRow.set((TableRow) v);
            });
            tableRow.setOnLongClickListener(v -> {
                // Load the image and set it to the ImageView
                Bitmap bitmap = BitmapFactory.decodeFile(car.getPicture_url());
                showImageDialog(bitmap);
                return false;
            });
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
        mViewModel = new ViewModelProvider(this).get(HistoryLicenseViewModel.class);
        // TODO: Use the ViewModel
    }

}