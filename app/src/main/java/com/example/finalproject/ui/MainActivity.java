package com.example.finalproject.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.GpsUtils;
import com.example.finalproject.R;
import com.example.finalproject.WifiDirectBroadcastReceiver;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView navigation;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
            }
        });

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.toggle_close, R.string.toggle_open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//        toggle.setDrawerIndicatorEnabled(false);
        requirePermissions();

        navigation = findViewById(R.id.navigation);
        final AppCompatActivity activity = this;

        NavController navController = Navigation.findNavController(activity, R.id.main_fragment);

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                NavController navController = Navigation.findNavController(activity, R.id.main_fragment);

                int menuItemId = menuItem.getItemId();
                int openFragmentId = navController.getCurrentDestination().getId();

                if(!(openFragmentId == R.id.findUserFragment && menuItemId == R.id.open_chat)
                        && !(openFragmentId == R.id.historyFragment && menuItemId == R.id.open_history)) {
                    if(menuItemId == R.id.open_chat) {
                        if(openFragmentId == R.id.historyFragment) {
                            navController.navigate(R.id.action_historyFragment_to_findUserFragment);
                        } else if(openFragmentId == R.id.messageFragment){
                            navController.navigate(R.id.action_messageFragment_to_findUserFragment);
                        }
                    } else if(menuItemId == R.id.open_history) {
                        if(openFragmentId == R.id.findUserFragment) {
                            navController.navigate(R.id.action_findUserFragment_to_historyFragment);
                        } else if(openFragmentId == R.id.messageFragment){
                            navController.navigate(R.id.action_messageFragment_to_historyFragment);
                        }
                    }
                }
                onBackPressed();
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(activity, R.id.main_fragment);
                int openFragmentId = navController.getCurrentDestination().getId();
                if(openFragmentId == R.id.messageFragment ) {
                    onBackPressed();
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }

//                NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
//                navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
            }
        });
    }

    private void requirePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {

                }
            });
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void disableToggle() {
        toggle.setDrawerIndicatorEnabled(false);
    }

    public void enableToggle() {
        toggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        int openFragmentId = navController.getCurrentDestination().getId();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (openFragmentId == R.id.findUserFragment) {
            navController.navigate(R.id.action_findUserFragment_to_historyFragment);
        } else if (openFragmentId == R.id.messageFragment) {
            navController.navigate(R.id.action_messageFragment_to_historyFragment);
        } else {
            super.onBackPressed();
        }
    }
}
