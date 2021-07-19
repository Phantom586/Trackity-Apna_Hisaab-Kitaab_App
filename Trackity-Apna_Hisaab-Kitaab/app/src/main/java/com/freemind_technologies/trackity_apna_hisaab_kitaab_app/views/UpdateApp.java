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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UpdateApp extends AppCompatActivity {

    private final String TAG = "UpdateAppActivity";
    private TextView tv_app_version, tv_app_new_version;
    private String app_version, new_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        tv_app_new_version = findViewById(R.id.ua_app_new_version);
        tv_app_version = findViewById(R.id.ua_app_version);

        Intent in = getIntent();
        app_version = "(" + in.getStringExtra("app_version");
        new_version = in.getStringExtra("new_version") + ")";

        tv_app_version.setText(app_version);
        tv_app_new_version.setText(new_version);

    }

    public void onUpdate(View view) {

        final String appPackageName = "com.freemind_technologies.trackity_apna_hisaab_kitaab_app";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}