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
import androidx.core.content.res.ResourcesCompat;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.load.engine.Resource;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DataHolder;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.SharedPrefsConstants;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public class Localization extends AppCompatActivity {

    private LinearLayout ll_locale_english, ll_locale_hindi;
    private TextView tv_en, tv_hi, tv_header, tv_disp_txt;
    private MySharedPreferences mySharedPreferences;
    private final String TAG = "Localization";
    private boolean isOnline = false;
    private String locale_str = "";
    private Button btn_continue;
    private boolean isFirstTime;
    private Utilities utilities;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization);

        ll_locale_english = findViewById(R.id.a_localize_english);
        mySharedPreferences = new MySharedPreferences(this);
        ll_locale_hindi = findViewById(R.id.a_localize_hindi);
        btn_continue = findViewById(R.id.a_localize_set_lang);
        tv_disp_txt = findViewById(R.id.a_localize_disp_text);
        tv_header = findViewById(R.id.a_localize_header);
        tv_en = findViewById(R.id.a_localize_en_text);
        tv_hi = findViewById(R.id.a_localize_hi_text);
        utilities = new Utilities(this);

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                isOnline = isConnected;
            }
        });

        Intent in = getIntent();
        isFirstTime = in.getStringExtra("isFirstTime").equals("true");

        if (!isFirstTime)
            tv_disp_txt.setVisibility(View.INVISIBLE);

        ll_locale_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_locale_hindi.setBackgroundResource(0);
                ll_locale_english.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.all_round_corners_blue, getApplicationContext().getTheme()));
                btn_continue.setVisibility(View.VISIBLE);
                locale_str = "en";
                utilities.updateLocale(Localization.this, locale_str);
                updateTexts();
            }
        });

        ll_locale_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_locale_english.setBackgroundResource(0);
                ll_locale_hindi.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.all_round_corners_blue, getApplicationContext().getTheme()));
                btn_continue.setVisibility(View.VISIBLE);
                locale_str = "hi";
                utilities.updateLocale(Localization.this, locale_str);
                updateTexts();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySharedPreferences.setFirstTime();

                if (!isFirstTime)
                    UpdateExpenseHeadsAndSubHeads();

                saveLocale(locale_str);
                Intent in = new Intent(Localization.this, MainActivity.class);
                in.putExtra("fragment_name", "none");
                startActivity(in);
            }
        });

    }

    private void UpdateExpenseHeadsAndSubHeads() {

        final DataHolder dataHolder = new DataHolder();
        final DBHelper dbHelper = new DBHelper(this);
        String expHeadsData;

        final String lang_code = mySharedPreferences.getStringParams(SharedPrefsConstants.USER_SELECTED_LANGUAGE_CODE,
                SharedPrefsConstants.DEFAULT_LANGUAGE_CODE);
        Log.d(TAG, "Current Language Code : "+lang_code+", Current Selected Lang : "+locale_str);

        if (!locale_str.equals(lang_code)) {

            Log.d(TAG, "Doing the Work!!");

            if (locale_str.equals("hi"))
                expHeadsData = dataHolder.expTypeList_Hi;
            else
                expHeadsData = dataHolder.expTypeList;

            try {

                JSONObject jsonObject = new JSONObject(expHeadsData);

                final JSONArray jsonArray = jsonObject.getJSONArray("Expense_Heads");

                int index = 0;

                final Cursor res = dbHelper.fetchAllExpenseTypes();

                while (res.moveToNext()) {

                    if (index < jsonArray.length()) {

                        final int arrIndex = res.getInt(0) - 1;
                        final JSONArray jsonArray1 = jsonArray.getJSONArray(arrIndex);
//                        Log.d(TAG, "Orig ExpType_ID : "+res.getString(0)+", ExpType_Name : "+res.getString(2));
//                        Log.d(TAG, "Updated ExpType_ID : "+jsonArray1.getString(0)+", ExpType_Name : "+jsonArray1.getString(2));

                        dbHelper.updateExpenseTypes__byID(res.getString(0), jsonArray1.getString(2));
                        index ++;

                    } else {
                        break;
                    }

                }

                res.close();

                final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                if (isOnline && isLoggedIn)
                    utilities.syncExpenseTypeData(TAG,  null, false, false, true, "true", true);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //Save locale method in preferences
    public void saveLocale(String lang) {
        MySharedPreferences sharedPref = new MySharedPreferences(this);
        sharedPref.setStringParams(SharedPrefsConstants.USER_SELECTED_LANGUAGE_CODE, lang);
    }

    private void updateTexts() {
        tv_header.setText(R.string.localize_header);
        tv_en.setText(R.string.localize_en);
        tv_hi.setText(R.string.localize_hi);
        if (isFirstTime)
            tv_disp_txt.setText(R.string.localize_disp_text);
        else
            tv_disp_txt.setVisibility(View.INVISIBLE);
        btn_continue.setText(R.string.localize_btn_text);
    }

}