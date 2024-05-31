package com.example.parking5.ui.system;

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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parking5.R;
import com.example.parking5.databinding.FragmentCameraSettingBinding;
import com.example.parking5.event.Var;
import com.example.parking5.util.ApacheServerReqeust;
import com.example.parking5.datamodel.Cam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class CameraSettingFragment extends Fragment {

    private CameraSettingViewModel mViewModel;
    private FragmentCameraSettingBinding binding;

    private static final int[] tableWeight = new int[]{1, 1, 2, 1, 1, 2};
    Var<TableRow> selectedRow;
    Vector<Cam> cams;
    public static CameraSettingFragment newInstance() {
        return new CameraSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(CameraSettingViewModel.class);

        binding = FragmentCameraSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedRow = new Var<>();
        cams = new Vector<>();
        tableSetting();

        Button btnAdd = binding.buttonCameraAdd;
        Button btnModify = binding.buttonCameraModify;
        Button btnDelete = binding.buttonCameraDelete;
        btnAdd.setOnClickListener((v) -> {
            showCamAddDialog();
        });
        btnModify.setOnClickListener((v) -> {
            showCamUpdateDialog();
        });
        btnDelete.setOnClickListener((v) -> {
            if (selectedRow.get() != null) {
                String ip = ((TextView) selectedRow.get().getChildAt(2)).getText().toString();
                deleteCam(ip);
            }
        });
        return root;
    }

    private void deleteCam(String ip) {
        Thread t = new Thread(() -> {
            ApacheServerReqeust.deleteCam(ip);
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void showCamUpdateDialog() {
        if (selectedRow.get() != null) {
            final View dialogView = View.inflate(getActivity(), R.layout.cam_setting, null);
            Dialog dialog = new Dialog(getActivity());
            TextView title = dialogView.findViewById(R.id.textView_title);
            title.setText(getString(R.string.cam_modify));
            String oldIp = ((TextView) selectedRow.get().getChildAt(2)).getText().toString();
            ((EditText) dialogView.findViewById(R.id.number_edittext)).setText(((TextView) selectedRow.get().getChildAt(0)).getText().toString());
            ((EditText) dialogView.findViewById(R.id.name_edittext)).setText(((TextView) selectedRow.get().getChildAt(1)).getText().toString());
            ((EditText) dialogView.findViewById(R.id.ip_edittext)).setText(oldIp);
            ((Spinner) dialogView.findViewById(R.id.mark_edittext)).setSelection(((TextView) selectedRow.get().getChildAt(3)).getText().toString().equals("入口") ? 0 : 1);
            Switch swPay = dialogView.findViewById(R.id.billing_gate);
            Switch swOpen = dialogView.findViewById(R.id.switch_default_open_gate);
            swPay.setChecked("是".equals(((TextView) selectedRow.get().getChildAt(4)).getText().toString()));
            swOpen.setChecked("是".equals(((TextView) selectedRow.get().getChildAt(5)).getText().toString()));
            dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
                try {
                    String number = ((EditText) dialogView.findViewById(R.id.number_edittext)).getText().toString();
                    String name = ((EditText) dialogView.findViewById(R.id.name_edittext)).getText().toString();
                    String newIp = ((EditText) dialogView.findViewById(R.id.ip_edittext)).getText().toString();
                    String in_out = ((Spinner) dialogView.findViewById(R.id.mark_edittext)).getSelectedItem().toString();
                    boolean pay = swPay.isChecked();
                    boolean open = swOpen.isChecked();
                    if (!number.isEmpty() && !name.isEmpty() && !newIp.isEmpty() && !in_out.isEmpty()) {
                        updateCam(Integer.parseInt(number), name, in_out, pay, open, oldIp, newIp);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }

    private void updateCam(int number, String name, String inOut, boolean pay, boolean open, String oldIp, String newIp) {
        Thread t = new Thread(() -> {
            if (oldIp.equals(newIp) || !checkCamExist(newIp)) {
                ApacheServerReqeust.updateCam(number, name, "入口".equals(inOut) ? 0 : 1, pay ? 1 : 0, open ? 1 : 0, oldIp, newIp);
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void showCamAddDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.cam_setting, null);
        Dialog dialog = new Dialog(getActivity());
        Switch swPay = dialogView.findViewById(R.id.billing_gate);
        Switch swOpen = dialogView.findViewById(R.id.switch_default_open_gate);
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            try {
                String number = ((TextView) dialogView.findViewById(R.id.number_edittext)).getText().toString();
                String name = ((TextView) dialogView.findViewById(R.id.name_edittext)).getText().toString();
                String ip = ((TextView) dialogView.findViewById(R.id.ip_edittext)).getText().toString();
                String in_out = ((Spinner) dialogView.findViewById(R.id.mark_edittext)).getSelectedItem().toString();
                boolean pay = swPay.isChecked();
                boolean open = swOpen.isChecked();
                if (!number.isEmpty() && !name.isEmpty() && !ip.isEmpty() && !in_out.isEmpty()) {
                    addCam(Integer.parseInt(number), name, ip, in_out, pay, open);
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void addCam(int number, String name, String ip, String inOut, boolean pay, boolean open) {
        Thread t = new Thread(() -> {
            if (!checkCamExist(ip)) {
                ApacheServerReqeust.addCam(number, name, ip, "入口".equals(inOut) ? 0 : 1, pay ? 0 : 1, open ? 0 : 1);
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableSetting();
    }

    private void tableSetting() {
        TableLayout tableCam = binding.tableCameraData;
        tableCam.removeAllViews();
        selectedRow.set(null);
        new Thread(() -> {
            getCams();
            getActivity().runOnUiThread(() -> {
                try {
                    // 遍历数据列表并为每行创建 TableRow
                    for (int i = 0; i < cams.size(); i++) {
                        TableRow tableRow = new TableRow(tableCam.getContext());
                        tableRow.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        Cam cam = cams.get(i);
                        // 为每行添加单元格
                        for (int j = 0; j < 6; j++) {
                            TextView textView = new TextView(tableRow.getContext());
                            textView.setPadding(5, 5, 5, 5);
                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, tableWeight[j]);
                            textView.setGravity(Gravity.CENTER);
                            textView.setLayoutParams(layoutParams);
                            switch (j) {
                                case 0:
                                    textView.setText(String.valueOf(cam.getNumber()));
                                    break;
                                case 1:
                                    textView.setText(cam.getName());
                                    break;
                                case 2:
                                    textView.setText(cam.getIp());
                                    break;
                                case 3:
                                    int in_out = cam.getIn_out();
                                    textView.setText(in_out == 0 ? getString(R.string.entrance) : getString(R.string.exit));
                                    break;
                                case 4:
                                    int pay = cam.getPay();
                                    textView.setText(pay == 0 ? getString(R.string.no) : getString(R.string.yes));
                                    break;
                                case 5:
                                    int open = cam.getOpen();
                                    textView.setText(open == 0 ? getString(R.string.no) : getString(R.string.yes));
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
                        tableCam.addView(tableRow);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private Vector<Cam> getCams() {
        try {
            String json = ApacheServerReqeust.getCams();
            JSONArray array = new JSONArray(json);
            if (array.length() > 0) {
                cams.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    cams.add(new Cam(obj.getInt("number"), obj.getString("ip"),
                            obj.getString("name"), obj.getInt("in_out"), obj.getInt("pay"), obj.getInt("open")));

                }
                return cams;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkCamExist(String ip) {
        try {
            String json = ApacheServerReqeust.getCam(ip);
            JSONArray array = new JSONArray(json);
            if (array.length() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CameraSettingViewModel.class);
        // TODO: Use the ViewModel
    }

}