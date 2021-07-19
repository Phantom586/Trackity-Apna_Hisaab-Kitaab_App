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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ResponseResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.SyncDBResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.SyncStatus;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.network.NetworkHandler;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.CustomSyncProgressDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utilities {

    private Context context;
    private DBHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private final String TAG = "Utilities";
    private CustomSyncProgressDialog syncProgressDialog;

    public Utilities(Context ctx) {

        this.context = ctx;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    }

    public boolean isDarkModeEnabled() {

        boolean enabled = false;

        final int nightModeFlags = this.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {

            case Configuration.UI_MODE_NIGHT_YES:
                enabled = true;
                break;

        }

        return enabled;

    }

    public List<String> getMonthStartEndDate(Calendar calendar, boolean trueLastDate) {

        final List<String> startEndDates = new ArrayList<>();
        String TrueLastDate = "";

        startEndDates.add(dateFormat.format(calendar.getTime()));

        final int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), maxDays);

        if (trueLastDate) {
            TrueLastDate = dateFormat.format(calendar.getTime());
        }

        // Adding 1 day to the month, so that the DB searches for all days not exclude the last one.
        calendar.add(Calendar.DATE, 1);

        startEndDates.add(dateFormat.format(calendar.getTime()));
        if (trueLastDate)
            startEndDates.add(TrueLastDate);

        return startEndDates;

    }

    public void showTopSnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg, int color) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, color));
        final Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        for (int i = 0; i < snackBarLayout.getChildCount(); i++) {
            View parent = snackBarLayout.getChildAt(i);
            if (parent instanceof LinearLayout) {
                ((LinearLayout) parent).setRotation(180);
                break;
            }
        }
        snackbar.show();

    }

    public void showBottomSnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg, int color) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, color));
        snackbar.show();

    }

    public void showInternetConnectionStatus__SnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.internet_status_color));
        snackbar.show();

    }

    public void syncExpenseTableData(String TAG, CustomSyncProgressDialog progressDialog, boolean store, boolean update, boolean delete, boolean fullDump, boolean silentMode) {

        dbHelper = new DBHelper(this.context);

        if (dbHelper.ExpenseTableHasData()) {

            try {

                final String sqliteDump = dbHelper.composeJSONFromSQLite__ExpenseTable(store, update, delete, fullDump);
                Log.d(TAG, "SQLite Dump :: ExpenseTable "+sqliteDump);

                try {

                    final String res = new BgWorker(this.context).execute("syncExpenseTableData", sqliteDump).get();
                    Log.d(TAG, "SyncExpenseTable Response : "+res);

                    final JSONObject jObj = new JSONObject(res.trim());

                    final JSONObject responseObj = jObj.getJSONObject("response");

                    final String responseCode = responseObj.getString("responseCode");

                    if (responseCode.equals("200")) {

                        final JSONArray jsonArray = jObj.getJSONArray("store_response");

                        for (int index = 0; index < jsonArray.length(); index ++) {

                            final JSONObject jObj1 = jsonArray.getJSONObject(index);

                            dbHelper.updateSyncStatus__ExpenseTable(jObj1.getString("id"), "Stored", jObj1.getString("status"));

                        }

                        final JSONArray jsonArray1 = jObj.getJSONArray("update_response");

                        for (int index = 0; index < jsonArray1.length(); index ++) {

                            final JSONObject jObj1 = jsonArray1.getJSONObject(index);

                            dbHelper.updateSyncStatus__ExpenseTable(jObj1.getString("id"), "Updated", jObj1.getString("status"));

                        }

                    }


                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

//                RequestBody data = RequestBody.create(sqliteDump, MediaType.parse("text/plain"));
//
//                Call<SyncDBResult> call = NetworkHandler.getNetworkHandler(this.context).getNetworkApi().syncExpenseTableData(data);
//                call.enqueue(new Callback<SyncDBResult>() {
//                    @Override
//                    public void onResponse(Call<SyncDBResult> call, Response<SyncDBResult> response) {
//                        Log.d(TAG, "onResponse: call" + response.isSuccessful());
//
//                        List<SyncStatus> storeResponse = response.body().getStoreResponse();
//                        List<SyncStatus> updateResponse = response.body().getUpdateResponse();
//                        ResponseResult result = response.body().getResponseResult();
//
//                        final String responseCode = result.getResponseCode();
//
//                        if (responseCode.equalsIgnoreCase("200")) {
//
//                            Log.d(TAG, "ExpenseData Stored in DB Successfully");
//
//                            if (storeResponse.size() > 0) {
//                                for (SyncStatus syncStatus: storeResponse) {
//
//                                    Log.d(TAG, "Stored :: ID -> "+syncStatus.getId()+ ", Status -> "+syncStatus.getStatus());
//                                    dbHelper.updateSyncStatus__ExpenseTable(syncStatus.getId(), "Stored", syncStatus.getStatus());
//
//                                }
//                            }
//
//                            if (updateResponse.size() > 0) {
//                                for (SyncStatus syncStatus: updateResponse) {
//
//                                    Log.d(TAG, "Update :: ID -> "+syncStatus.getId()+ ", Status -> "+syncStatus.getStatus());
//                                    dbHelper.updateSyncStatus__ExpenseTable(syncStatus.getId(), "Updated", syncStatus.getStatus());
//
//                                }
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<SyncDBResult> call, Throwable t) {
//
//                        if (!silentMode) progressDialog.hide();
//                        Log.d(TAG, "Failed Somehow");
//                    }
//                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void syncExpenseTypeData(String TAG, CustomSyncProgressDialog progressDialog, boolean store, boolean delete, boolean fullDump, String lang_change, boolean silentMode) {

        dbHelper = new DBHelper(this.context);

        try {

            final String sqliteDump = dbHelper.composeJSONFromSQLite__ExpenseTypes(store, delete, fullDump);
            Log.d(TAG, "SQLite Dump :: ExpenseTypes "+sqliteDump);

            try {

                final String res = new BgWorker(this.context).execute("syncExpenseTypeData", sqliteDump, lang_change).get();
                Log.d(TAG, "syncExpenseTypeData Response : "+res);

                final JSONObject jObj = new JSONObject(res.trim());

                final JSONObject responseObj = jObj.getJSONObject("response");

                final String responseCode = responseObj.getString("responseCode");

                if (responseCode.equals("200")) {

                    final JSONArray jsonArray = jObj.getJSONArray("store_response");

                    for (int index = 0; index < jsonArray.length(); index ++) {

                        final JSONObject jObj1 = jsonArray.getJSONObject(index);

                        dbHelper.updateSyncStatus__ExpenseTypes(jObj1.getString("id"), jObj1.getString("status"));

                    }

                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

//            RequestBody data = RequestBody.create(sqliteDump, MediaType.parse("text/plain"));
//
//            Call<SyncDBResult> call = NetworkHandler.getNetworkHandler(this.context).getNetworkApi().syncExpenseTypeData(data);
//            call.enqueue(new Callback<SyncDBResult>() {
//                @Override
//                public void onResponse(Call<SyncDBResult> call, Response<SyncDBResult> response) {
//                    Log.d(TAG, "onResponse: call" + response.isSuccessful());
//
//                    List<SyncStatus> storeResponse = response.body().getStoreResponse();
//                    ResponseResult result = response.body().getResponseResult();
//
//                    final String responseCode = result.getResponseCode();
//
//                    if (responseCode.equalsIgnoreCase("200")) {
//
//                        Log.d(TAG, "ExpenseTypes Stored in DB Successfully");
//
//                        for (SyncStatus syncStatus: storeResponse) {
//
//                            Log.d(TAG, "ID -> "+syncStatus.getId()+ ", Status -> "+syncStatus.getStatus());
//                            dbHelper.updateSyncStatus__ExpenseTypes(syncStatus.getId(), syncStatus.getStatus());
//
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<SyncDBResult> call, Throwable t) {
//
//                    if (!silentMode) progressDialog.hide();
//
//                    Log.d(TAG, "Failed Somehow");
//                }
//            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void syncData(Context context, CoordinatorLayout coordinatorLayout) {

        syncProgressDialog = new CustomSyncProgressDialog(context);

        syncExpenseTableData(TAG, syncProgressDialog, false, false, false, true, false);

        syncExpenseTypeData(TAG, syncProgressDialog, false, false, true, "false", false);
        syncProgressDialog.hide();

        showBottomSnackBar(context, coordinatorLayout, context.getResources().getString(R.string.util_data_sync_successful),
                R.color.internet_status_color);

    }

    public void updateLocale(Context ctx, String lang) {

        Locale myLocale = new Locale(lang);//Set Selected Locale
        Locale.setDefault(myLocale);//set new locale as default
        Resources resources = ctx.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(myLocale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }

}
