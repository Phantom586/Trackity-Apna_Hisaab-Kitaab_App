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

import androidx.appcompat.app.AppCompatActivity;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.BgWorker;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.SharedPrefsConstants;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = "SplashScreen";
    private ImageView im_app_logo;
    private TextView tv_app_name;
    private Animation logo_anim;
    private CharSequence charSequence;
    private int index = 0;
    private long delay = 100;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MySharedPreferences mySharedPreferences;
    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo_anim = AnimationUtils.loadAnimation(this, R.anim.ss_logo);
        mySharedPreferences = new MySharedPreferences(this);
        im_app_logo = findViewById(R.id.ss_app_logo);
        im_app_logo.startAnimation(logo_anim);
        tv_app_name = findViewById(R.id.ss_app_name);

        final String lang_code = mySharedPreferences.getStringParams(SharedPrefsConstants.USER_SELECTED_LANGUAGE_CODE,
                SharedPrefsConstants.DEFAULT_LANGUAGE_CODE);
        utilities = new Utilities(this);
        if (!lang_code.equals("en")) {
            utilities.updateLocale(this, lang_code);
            setContentView(R.layout.activity_splash_screen);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    final String type = "retrieveAppVersion";
                    final String res = new BgWorker(SplashScreen.this).execute(type, "App_Version").get();
                    Log.d(TAG, "Current App Version : "+res);

                    JSONArray jsonArray = new JSONArray(res.trim());
                    JSONObject jobj = jsonArray.getJSONObject(1);

                    final String curr_app_version = jobj.getString("App_Version");
                    Log.d(TAG, "Current App Version : "+curr_app_version);

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    Log.d(TAG, "App Version : "+version);

                    final double device_app_version = Double.parseDouble(version);
                    final double current_app_version = Double.parseDouble(curr_app_version);

                    if (device_app_version < current_app_version) {

//                finish();
                        Intent in = new Intent(SplashScreen.this, UpdateApp.class);
                        in.putExtra("app_version", version);
                        in.putExtra("new_version", curr_app_version);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(in);

                    } else {

                        final boolean firstTime = mySharedPreferences.isFirstTime();
                        Intent in;
                        if (firstTime) {
                            in = new Intent(SplashScreen.this, Localization.class);
                            in.putExtra("isFirstTime", "true");
                        }
                        else
                            in = new Intent(SplashScreen.this, MainActivity.class);
                        in.putExtra("fragment_name", "none");
                        startActivity(in);
                        finish();

                    }

                } catch (ExecutionException | InterruptedException | JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }, 1600);

        animatedLogoName(getResources().getString(R.string.name_app));

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            tv_app_name.setText(charSequence.subSequence(0, index++));

            if (index <= charSequence.length()) {
                handler.postDelayed(runnable, delay);
            }

        }
    };

    private void animatedLogoName(CharSequence cs) {

        charSequence = cs;
        index = 0;
        tv_app_name.setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);

    }

}