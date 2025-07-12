package com.example.walletapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar first
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navController == null) {
            setupNavigation();
        }
    }

    private void setupNavigation() {
        try {
            // Setup navigation controller
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            // Setup app bar configuration
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

            // Connect toolbar with navigation controller
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        } catch (IllegalStateException e) {
            // NavController not ready yet, will try again in next onResume
            navController = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}