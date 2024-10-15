package com.example.parking5.ui.data_search;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.example.parking5.util.ApacheServerRequest;
import com.example.parking5.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/***
 * show all the cars inside the lot
 */
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
        Button btnAdd = binding.buttonAdd;
        Button btnDelete = binding.buttonDelete;
        Button btnSearch = binding.buttonSearch;
        btnModify.setOnClickListener(v -> {
            showModifyDialog();
        });
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

    private void showAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.car_inside_add, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtTitle = dialogView.findViewById(R.id.textView_title);
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
                    addCarInside(number, time);
                }
                tableSetting();
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();

    }

    private void addCarInside(String number, String start) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.addCarInsideWithCarNumber(number, start);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCarInside(String number) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            String res = ApacheServerRequest.getCarInsideWithCarNumber(number);
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

    private void showSearchDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.car_number_modify_search, null);
        Dialog dialog = new Dialog(getActivity());
        TextView txtCarNumber = dialogView.findViewById(R.id.car_number_edittext);
        TextView txtStart = dialogView.findViewById(R.id.textView_start);
        TextView txtEnd = dialogView.findViewById(R.id.textView_end);
        txtStart.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtStart);
        });
        txtEnd.setOnClickListener(v -> {
            Util.showDateTimeDialog(getActivity(), txtEnd);
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            if (!txtCarNumber.getText().toString().isEmpty() && !txtStart.getText().toString().isEmpty() && !txtEnd.getText().toString().isEmpty()) {
                String number = txtCarNumber.getText().toString();
                String start = txtStart.getText().toString();
                String end = txtEnd.getText().toString();
                searchCars(number, start, end);
            }

        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void searchCars(String number, String start, String end) {
        tableSetting(number, start, end);
    }

    private void showModifyDialog() {
        if (selectedRow.get() != null) {
            final View dialogView = View.inflate(getActivity(), R.layout.car_number_modify, null);
            Dialog dialog = new Dialog(getActivity());
            TextView txtCarNumber = dialogView.findViewById(R.id.car_number_edittext);
            TextView txtStart = dialogView.findViewById(R.id.textView_entrance_time);
            ImageView imageView = dialogView.findViewById(R.id.imageView_car_number);
            TableLayout table = binding.tableCarData;
            int index = table.indexOfChild(selectedRow.get());
            Bitmap bitmap = ApacheServerRequest.getPictureByPath(cars.get(index).getPicture_url());
            imageView.setImageBitmap(bitmap);
            txtCarNumber.setText(cars.get(index).getCar_number());
            txtStart.setText(cars.get(index).getTime_in());
            txtStart.setOnClickListener(v -> {
                Util.showDateTimeDialog(getActivity(), txtStart);
            });
            dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
            dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
                if (!txtCarNumber.getText().toString().isEmpty() && !txtStart.getText().toString().isEmpty()) {
                    String old_number = cars.get(index).getCar_number();
                    String number = txtCarNumber.getText().toString();
                    String start = txtStart.getText().toString();
                    updateCarInsideNumber(old_number, number, start);
                    tableSetting();
                    dialog.dismiss();
                }

            });
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }

    private void deleteCarInside() {
        if (selectedRow.get() != null) {
            TableLayout table = binding.tableCarData;
            int index = table.indexOfChild(selectedRow.get());
            deleteCarInsideData(cars.get(index).getCar_number());
            tableSetting();
        }
    }

    private void tableSetting() {
        tableSetting("", "", "");
    }

    private void tableSetting(String carNumber, String start, String end) {
        TableLayout tableData = binding.tableCarData;
        tableData.removeAllViews();
        selectedRow.set(null);
        getCarsWithDates(carNumber, start, end);
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
                        String number = car.getCar_number();
                        if (Util.getRegularCar(number) != null) {
                            number += "(月租)";
                        }
                        textView.setText(number);
                        if (!number.matches(".*\\d{4}.*")) {
                            textView.setTextColor(Color.RED);
                        }
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
                Bitmap bitmap = ApacheServerRequest.getPictureByPath(car.getPicture_url());
                if (bitmap != null) {
                    showImageDialog(bitmap);
                }
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

    private void getCarsWithDates(String carNumber, String start, String end) {
        Thread t = new Thread(() -> {
            String json = "";
            if (!carNumber.isEmpty() && !start.isEmpty()) {
                json = ApacheServerRequest.getCarInsideWithDatesAndCarNumber(carNumber, start, end);
            } else if (!start.isEmpty()) {
                json = ApacheServerRequest.getCarInsideWithDates(start, end);
            } else if (!carNumber.isEmpty()) {
                json = ApacheServerRequest.getCarInsideWithCarNumber(carNumber);
            } else {
                json = ApacheServerRequest.getCarInside();
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

    private void deleteCarInsideData(String carNumber) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.deleteCarInside(carNumber);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCarInsideNumber(String old_number, String new_number, String timeIn) {
        Thread t = new Thread(() -> {
            ApacheServerRequest.updateCarInsideNumber(old_number, new_number, timeIn);
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