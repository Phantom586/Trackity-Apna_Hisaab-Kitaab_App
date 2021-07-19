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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String PREFS_NAME = "FM_TR_HK";
    private SharedPreferences prefs;
    private Context context;

    public MySharedPreferences(Context ctx) { this.context = ctx; }

    public String getLoginMethod() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String res = prefs.getString("loginMethod", "");
        return res;

    }

    public void setLoginMethod(String method){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMethod", method);
        editor.apply();

    }

    public boolean getExpTypesStored() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean("expTypeStored", false);
        return res;

    }

    public void setUpdateDone(boolean status) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("updateDone", status);
        editor.apply();

    }

    public boolean isUpdateDone() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean("updateDone", false);
        return res;

    }

    public void setExpTypesStored(boolean status) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("expTypeStored", status);
        editor.apply();

    }

    public boolean isLoggedIn() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean("isLoggedIn", false);
        return res;

    }

    public void setLoggedIn(boolean status){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", status);
        editor.apply();

    }

    public String getUserID() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String res = prefs.getString("User_ID", "");
        return res;

    }

    public void setUserID(String id){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User_ID", id);
        editor.apply();

    }

    public boolean isDiscovered(String key) {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean(key, false);
        return res;

    }

    public void setDiscovered(String key, boolean status){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, status);
        editor.apply();

    }

    public String getStringParams(String a_ParamName, String defaultValue) {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(a_ParamName, defaultValue);

    }

    public void setStringParams(String a_ParamName, String a_ParamValue) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(a_ParamName, a_ParamValue);
        editor.apply();
    }

    public boolean isFirstTime() {

        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean("isFirstTime", true);
        return res;

    }

    public void setFirstTime(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();

    }
    
}
