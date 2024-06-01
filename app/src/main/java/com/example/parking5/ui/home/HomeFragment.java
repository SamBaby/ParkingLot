package com.example.parking5.ui.home;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.parking5.R;
import com.example.parking5.data.ConfigurationString;
import com.example.parking5.databinding.FragmentHomeBinding;
import com.example.parking5.datamodel.Cam;
import com.example.parking5.util.ApacheServerReqeust;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView txtLots;
    private TextView txtBills;
    private TextView txtDay;
    private TextView txtMonth;
    private Button btnAbnormalCar;
    private Button btnExtract;
    private Vector<Cam> cams;
    private Vector<ToggleButton> channelButtons = new Vector<>();
    private VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtLots = binding.textViewLotsLeft;
        txtBills = binding.textViewBillLeft;
        txtDay = binding.textViewRevenueDay;
        txtMonth = binding.textViewRevenueMonth;
        btnAbnormalCar = binding.buttonAbnormalLicense;
        btnExtract = binding.buttonRevenueExtract;
        videoView = binding.videoView;

        txtLots.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_revenueFragment);
            }

        });
        txtBills.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_revenueFragment);
            }

        });
        txtDay.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConfigurationString.todayRevenue, true);
                navController.navigate(R.id.action_homeFragment_to_revenueFragment, bundle);
            }

        });
        txtMonth.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_revenueFragment);
            }

        });
        btnExtract.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_revenueFragment);
            }

        });
        btnAbnormalCar.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_homeFragment_to_dataSearchFragment);
            }

        });
//        final TextView textView = binding.textHome;
        homeViewModel.getLots().observe(getViewLifecycleOwner(), txtLots::setText);
//        txtBills
//        txtDay
//        txtMonth
        cams = getCams();
        setChannelTable();
        setButtons();
        return root;
    }

    private void setButtons() {
        Button in = binding.remoteOpenIn;
        Button out = binding.remoteOpenOut;
    }

    private void setChannelTable() {
        TableLayout tableLayout = binding.tableChannel;

        TableRow tableRow = new TableRow(tableLayout.getContext());
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        if (cams == null) {
            return;
        }
        for (int i = 1; i <= cams.size(); i++) {
            // 创建并设置按钮
            try {
                Cam cam = cams.get(i - 1);
                ToggleButton toggleButton = new ToggleButton(tableRow.getContext());
                toggleButton.setTextOn("CH" + String.valueOf(i));
                toggleButton.setTextOff("CH" + String.valueOf(i));
                toggleButton.setText("CH" + String.valueOf(i));
                if (i == 1) {
                    toggleButton.setChecked(true);
                    toggleButton.setTextColor(Color.BLACK);
                    playRTSP(cam.getIp());
                } else {
                    toggleButton.setTextColor(Color.GRAY);
                }
                toggleButton.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                toggleButton.setOnClickListener(v -> {
                    playRTSP(cam.getIp());
                    toggleButton.setTextColor(Color.BLACK);
                    for (ToggleButton btn : channelButtons) {
                        if (!btn.equals(toggleButton)) {
                            btn.setTextColor(Color.GRAY);
                        }
                    }
                });
                // 添加 ToggleButton 到 TableRow
                tableRow.addView(toggleButton);
                channelButtons.add(toggleButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 将 TableRow 添加到 TableLayout
        tableLayout.addView(tableRow);
    }

    private void playRTSP(String ip) {
        Uri url = Uri.parse(ip);
        videoView.setVideoURI(url);
        videoView.start();
    }

    private Vector<Cam> getCams() {
        Vector<Cam> cams = new Vector<>();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}