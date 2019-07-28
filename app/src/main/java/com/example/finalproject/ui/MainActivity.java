package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView navigation;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.toggle_close, R.string.toggle_open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        int openFragmentId = navController.getCurrentDestination().getId();

        if(drawer.isDrawerOpen(GravityCompat.START)) {
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
