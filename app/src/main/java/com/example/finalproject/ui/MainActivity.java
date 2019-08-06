package com.example.finalproject.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.Helper;
import com.example.finalproject.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView navigation;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ConstraintLayout permissionsOverlay;
    Button retryPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        permissionsOverlay = findViewById(R.id.require_permissions);
        permissionsOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        retryPermissions = findViewById(R.id.retry_permissions);
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
        navigation = findViewById(R.id.navigation);
        final AppCompatActivity activity = this;

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (!allPermissionsSatisfied()) return true;

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

        retryPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requirePermissions();
            }
        });

        requirePermissions();
    }

    private boolean isWifiOn = false;
    private boolean isGPSOn = false;

    private boolean allPermissionsSatisfied() {
        return isGPSOn && isWifiOn;
    }

    private void checkStart() {
        if (allPermissionsSatisfied()) {
            permissionsOverlay.setVisibility(View.GONE);
        }
    }

    private void requirePermissions() {

        Helper.turnWifiOn(this, new Helper.PermissionListener() {
            @Override
            public void onSuccess() {
                isWifiOn = true;
                checkStart();
            }

            @Override
            public void onFailure() {
                isWifiOn = false;
            }
        });

        Helper.turnGpsOn(this, new Helper.PermissionListener() {
            @Override
            public void onSuccess() {
                isGPSOn = true;
                checkStart();
            }

            @Override
            public void onFailure() {
                isGPSOn = false;
            }
        });
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
            SocketHandler.stopSocket();
            navController.navigate(R.id.action_messageFragment_to_historyFragment);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketHandler.stopSocket();
    }
}
