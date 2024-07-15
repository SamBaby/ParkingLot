package com.example.parking5;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.parking5.data.ConfigurationString;
import com.example.parking5.data.LoginRepository;
import com.example.parking5.databinding.ActivityMainBinding;
import com.example.parking5.datamodel.User;
import com.example.parking5.ui.login.LoginActivity;
import com.example.parking5.util.ApacheServerRequest;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the user is logged in
        boolean isLoggedIn = checkIfUserIsLoggedIn(); // Implement this method
        if (!isLoggedIn) {
            // User is not logged in, navigate to the login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.appBarMain.toolbar);
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_system, R.id.nav_revenue, R.id.nav_data_search)
                    .setOpenableLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }
    }

    private boolean checkIfUserIsLoggedIn() {
        if (LoginRepository.getInstance() != null) {
            return LoginRepository.getInstance().isLoggedIn();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_password) {
            showAccountChangePasswordDialog();
            return true;
        } else if (id == R.id.logout) {
            if (LoginRepository.getInstance() != null) {
                LoginRepository.getInstance().logout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                return false;
            }

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showAccountChangePasswordDialog() {
        if (LoginRepository.getInstance() == null) {
            return;
        }
        User user = LoginRepository.getInstance().getLoggedInUser();

        final View dialogView = View.inflate(this, R.layout.password_change, null);
        Dialog dialog = new Dialog(this);
        dialogView.findViewById(R.id.confirm_button).setOnClickListener((v) -> {
            String current_password = String.valueOf(((TextView) dialogView.findViewById(R.id.current_password_edittext)).getText());
            String new_password = String.valueOf(((TextView) dialogView.findViewById(R.id.new_password_editText)).getText());
            String confirm_password = String.valueOf(((TextView) dialogView.findViewById(R.id.confirm_password_editText)).getText());
            if (current_password.isEmpty()) {
                Toast.makeText(getApplicationContext(), ConfigurationString.currentPasswordNull, Toast.LENGTH_SHORT).show();
                return;
            } else if (new_password.isEmpty()) {
                Toast.makeText(getApplicationContext(), ConfigurationString.newPasswordNull, Toast.LENGTH_SHORT).show();
                return;
            } else if (confirm_password.isEmpty()) {
                Toast.makeText(getApplicationContext(), ConfigurationString.confirmPasswordNull, Toast.LENGTH_SHORT).show();
                return;
            } else if (!user.getPassword().equals(current_password)) {
                Toast.makeText(getApplicationContext(), ConfigurationString.currentPasswordNotSame, Toast.LENGTH_SHORT).show();
                return;
            } else if (!new_password.equals(confirm_password)) {
                Toast.makeText(getApplicationContext(), ConfigurationString.newConfirmPasswordNotSame, Toast.LENGTH_SHORT).show();
                return;
            }
            changePassword(LoginRepository.getInstance().getLoggedInUser().getAccount(), new_password);
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener((v) -> dialog.dismiss());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void changePassword(String account, String password) {

        try {
            Thread t = new Thread(() -> {
                ApacheServerRequest.changeUserPassword(account, password);
            });
            t.start();
            t.join();
        } catch (Exception e) {

        }
    }

}