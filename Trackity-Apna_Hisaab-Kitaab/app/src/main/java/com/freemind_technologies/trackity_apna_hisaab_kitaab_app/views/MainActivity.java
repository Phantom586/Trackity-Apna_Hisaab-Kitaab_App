/*
 * Copyright 2021 FreeMind Technologies. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.bottomNavFragments.CalendarFragment;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.bottomNavFragments.HomeFragment;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.bottomNavFragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MySharedPreferences mySharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private final String TAG = "MainActivity";
    private boolean isOnline = false;
    private Boolean exit = false;
    private String openFragment;
    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.ah_bottom_nav_menu);
        coordinatorLayout = findViewById(R.id.ma_coordinator_layout);
        mySharedPreferences = new MySharedPreferences(this);
        bottomNavigationView.setSelectedItemId(R.id.hbn_home);
        utilities = new Utilities(this);

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){

                utilities.showInternetConnectionStatus__SnackBar(getApplicationContext(), coordinatorLayout, isConnected);

                isOnline = isConnected;

                final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                if (isLoggedIn && isConnected)
                    utilities.syncData(MainActivity.this, coordinatorLayout);

            }
        });

        Intent in = getIntent();
        openFragment = in.getStringExtra("fragment_name");

        if (openFragment.equals("none") || openFragment.equals("home"))
            getSupportFragmentManager().beginTransaction().replace(R.id.ah_frame_layout, new HomeFragment()).commit();
        else if (openFragment.equals("calendar")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.ah_frame_layout, new CalendarFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.hbn_calendar);
        } else if (openFragment.equals("profile")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.ah_frame_layout, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.hbn_profile);
        }

        setUpBottomMenu();

    }

    private void setUpBottomMenu() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                boolean flag = true;

                final int current_selected_item_id = bottomNavigationView.getSelectedItemId();

                // Making Sure that the same fragment isn't opened again.
                if (item.getItemId() == R.id.hbn_home && current_selected_item_id != R.id.hbn_home) {
                    fragment = new HomeFragment();
                } else if (item.getItemId() == R.id.hbn_calendar && current_selected_item_id != R.id.hbn_calendar) {
                    fragment = new CalendarFragment();
                } else if (item.getItemId() == R.id.hbn_profile && current_selected_item_id != R.id.hbn_profile) {
                    fragment = new ProfileFragment();
                } else {
                    flag = false;
                }

                if (flag) {
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .setCustomAnimations(R.anim.fade_in_from_bottom, R.anim.fade_out_from_top)
//                            .replace(R.id.ah_frame_layout, fragment)
//                            .commit();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.ah_frame_layout, fragment)
                            .commit();
                }

                return true;

            }
        });

    }

    @Override
    public void onBackPressed() {

        if (exit) {
            moveTaskToBack(true);
        } else {

            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

}