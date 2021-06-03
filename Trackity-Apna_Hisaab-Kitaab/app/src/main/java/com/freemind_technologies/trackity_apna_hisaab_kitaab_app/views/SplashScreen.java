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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private ImageView im_app_logo;
    private TextView tv_app_name;
    private Animation logo_anim;
    private CharSequence charSequence;
    private int index = 0;
    private long delay = 100;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo_anim = AnimationUtils.loadAnimation(this, R.anim.ss_logo);
        im_app_logo = findViewById(R.id.ss_app_logo);
        im_app_logo.startAnimation(logo_anim);
        tv_app_name = findViewById(R.id.ss_app_name);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(SplashScreen.this, MainActivity.class);
                in.putExtra("fragment_name", "none");
                startActivity(in);
            }
        }, 1600);

        animatedLogoName("Trackity");

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