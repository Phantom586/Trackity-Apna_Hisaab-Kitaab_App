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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.bottomNavFragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ImportDataResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.RegisterResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.RegisterUser;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ResponseResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.VerifyUserResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.BgWorker;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.SharedPrefsConstants;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.network.NetworkHandler;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.CustomImportProgressDialog;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.CustomSyncProgressDialog;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.ExpenseSummary;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.Localization;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.ManageExpenseTypes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private LinearLayout ll_manage_exp_types, ll_logout, ll_import, ll_sync_db, ll_google_signIn,
            ll_expense_summary, ll_feedback, ll_change_localization;
    private TextView tv_user_name, tv_month_name, tv_total_amt_month_name, tv_total_month_amt,
            tv_new_exp_type, tv_new_exp_summary;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 234;
    private CustomImportProgressDialog importProgressDialog;
    private CustomSyncProgressDialog syncProgressDialog;
    private ImageView im_month_back, im_month_forward;
    private MySharedPreferences mySharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private final String TAG = "ProfileFragment";
    private CoordinatorLayout coordinatorLayout;
    private MySharedPreferences mySharedPrefs;
    private LineChart total_expenses_chart;
    private FirebaseUser firebaseUser;
    private boolean isOnline = false;
    private ImageView im_profile_pic;
    private List<String> monthsList;
    private Utilities utilities;
    private FirebaseAuth mAuth;
    private DBHelper dbHelper;
    private int month_no = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        total_expenses_chart = view.findViewById(R.id.fp_total_expenses_bar_chart);
        ll_change_localization = view.findViewById(R.id.fp_change_localization);
        tv_total_amt_month_name = view.findViewById(R.id.fp_month_exp_name);
        tv_total_month_amt = view.findViewById(R.id.fp_total_month_amount);
        ll_manage_exp_types = view.findViewById(R.id.fp_manage_exp_types);
        coordinatorLayout = view.findViewById(R.id.fp_coordinatorLayout);
        ll_expense_summary = view.findViewById(R.id.fp_expense_summary);
        tv_new_exp_summary = view.findViewById(R.id.fp_es_first_time);
        ll_google_signIn = view.findViewById(R.id.fp_google_signIn);
        im_month_forward = view.findViewById(R.id.fp_month_forward);
        tv_new_exp_type = view.findViewById(R.id.fp_met_first_time);
        mySharedPreferences = new MySharedPreferences(getContext());
        im_profile_pic = view.findViewById(R.id.fh_profile_pic);
        im_month_back = view.findViewById(R.id.fp_month_back);
        mySharedPrefs = new MySharedPreferences(getContext());
        tv_month_name = view.findViewById(R.id.fp_month_name);
        tv_user_name = view.findViewById(R.id.fh_user_name);
//        ll_feedback = view.findViewById(R.id.fp_feedback);
//        ll_sync_db = view.findViewById(R.id.fp_sync_db);
        ll_logout = view.findViewById(R.id.fp_logout);
//        ll_import = view.findViewById(R.id.fp_import);
        utilities = new Utilities(getContext());
        dbHelper = new DBHelper(getContext());
        monthsList = new ArrayList<>();

        importProgressDialog = new CustomImportProgressDialog(getContext());
        syncProgressDialog = new CustomSyncProgressDialog(getContext());

        Tovuti.from(getContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                isOnline = isConnected;
            }
        });

        initLineChart();

        // Initializing Sign in options
        GoogleSignInOptions gsop = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gsop);
        mAuth = FirebaseAuth.getInstance();

        init();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            ll_google_signIn.setVisibility(View.GONE);
//            ll_sync_db.setVisibility(View.VISIBLE);
//            ll_import.setVisibility(View.VISIBLE);
            ll_logout.setVisibility(View.VISIBLE);
            setUserDetails(firebaseUser);
        }

        setup();

        calculateMonthlyExpense();

        return view;

    }

    private void initLineChart() {

        total_expenses_chart.getDescription().setEnabled(false);
        total_expenses_chart.animateY(800);
        total_expenses_chart.setDragEnabled(true);
        total_expenses_chart.setScaleEnabled(false);
        total_expenses_chart.setPinchZoom(false);
        total_expenses_chart.setExtraLeftOffset(15);
        total_expenses_chart.setExtraRightOffset(15);
        // Hiding Background Lines
        total_expenses_chart.setDrawBorders(false);
        total_expenses_chart.setDrawGridBackground(false);
        total_expenses_chart.getAxisLeft().setDrawGridLines(false);
        total_expenses_chart.getAxisRight().setDrawGridLines(false);
        // Hiding right Y and top X border
        YAxis rightAxis = total_expenses_chart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = total_expenses_chart.getAxisLeft();
        leftAxis.setEnabled(false);
        XAxis topXAxis = total_expenses_chart.getXAxis();
        topXAxis.setEnabled(false);

    }

    private void init() {

        boolean isDiscovered = mySharedPrefs.isDiscovered("discoverMET");
        if (isDiscovered)
            tv_new_exp_type.setVisibility(View.INVISIBLE);
        isDiscovered = mySharedPrefs.isDiscovered("discoverES");
        if (isDiscovered)
            tv_new_exp_summary.setVisibility(View.INVISIBLE);

        ll_google_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline)
                    signIn();
                else {
                    String msg = getResources().getString(R.string.util_internet_not_connected);
                    utilities.showInternetConnectionStatus__SnackBar(getContext(), coordinatorLayout, msg);
                }
            }
        });

        ll_manage_exp_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySharedPrefs.setDiscovered("discoverMET", true);
                Intent in = new Intent(v.getContext(), ManageExpenseTypes.class);
                in.putExtra("comingFrom", "ProfileFragment");
                v.getContext().startActivity(in);
            }
        });

//        ll_feedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(v.getContext(), Feedback.class);
//                v.getContext().startActivity(in);
//            }
//        });

//        ll_sync_db.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isOnline)
//                    syncUserDataToDB();
//                else
//                    utilities.showInternetConnectionStatus__SnackBar(getContext(), coordinatorLayout, isOnline);
//            }
//        });

        ll_change_localization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(), Localization.class);
                in.putExtra("isFirstTime", "false");
                v.getContext().startActivity(in);
            }
        });

        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline)
                    logoutUser();
                else {
                    String msg = getResources().getString(R.string.util_internet_not_connected);
                    utilities.showInternetConnectionStatus__SnackBar(getContext(), coordinatorLayout, msg);
                }
            }
        });

//        ll_import.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isOnline)
//                    ImportDataFromServerDB();
//                else
//                    utilities.showInternetConnectionStatus__SnackBar(getContext(), coordinatorLayout, isOnline);
//            }
//        });

        ll_expense_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySharedPrefs.setDiscovered("discoverES", true);
                Intent in = new Intent(v.getContext(), ExpenseSummary.class);
                v.getContext().startActivity(in);
            }
        });

        im_month_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (month_no == 11)
                    im_month_forward.setVisibility(View.VISIBLE);
                if (month_no > 1) {
                    month_no -= 1;
                } else if (month_no == 1) {
                    month_no -= 1;
                    im_month_back.setVisibility(View.INVISIBLE);
                }
                String month_name = monthsList.get(month_no);
                tv_month_name.setText(month_name);
                month_name += "'s";
                tv_total_amt_month_name.setText(month_name);
                tv_total_month_amt.setText("0");
                calculateWeeklyExpenseData();
                calculateMonthlyExpense();
            }
        });

        im_month_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (month_no == 0)
                    im_month_back.setVisibility(View.VISIBLE);
                if (month_no < 10) {
                    month_no += 1;
                } else if (month_no == 10) {
                    month_no += 1;
                    im_month_forward.setVisibility(View.INVISIBLE);
                }
                String month_name = monthsList.get(month_no);
                tv_month_name.setText(month_name);
                month_name += "'s";
                tv_total_amt_month_name.setText(month_name);
                tv_total_month_amt.setText("0");
                calculateWeeklyExpenseData();
                calculateMonthlyExpense();
            }
        });

    }

    ActivityResultLauncher<Intent> googleSignIn = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Log.d(TAG, "Request Code Verified");
                        Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                        if (signInAccountTask.isSuccessful()) {
                            Log.d(TAG, "Sign In Successful");
                            try {

                                GoogleSignInAccount account = signInAccountTask.getResult(ApiException.class);

                                if (account != null) {
                                    firebaseAuthWithGoogle(account);
                                }

                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "Sign In Failed");
                        }

                    }

                }
            }
    );

    private void signIn() {

        Intent in = googleSignInClient.getSignInIntent();
        googleSignIn.launch(in);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(
                        getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    mySharedPrefs.setLoginMethod("Google");

                                    setUserDetails(user);
                                    checkIfUserAlreadyExistsInDB(user);

                                    mySharedPrefs.setLoggedIn(true);

//                                    ll_sync_db.setVisibility(View.VISIBLE);
//                                    ll_import.setVisibility(View.VISIBLE);
                                    ll_logout.setVisibility(View.VISIBLE);

                                    ll_google_signIn.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

    }

    private void checkIfUserAlreadyExistsInDB(FirebaseUser user) {

//        RequestBody u_email = RequestBody.create(user.getEmail(), MediaType.parse("text/plain"));

        try {

            final String res = new BgWorker(getContext()).execute("verifyUser", user.getEmail()).get();
            Log.d(TAG, "Response : "+res);

            final JSONObject jsonObject = new JSONObject(res.trim());

            final JSONObject responseObj = jsonObject.getJSONObject("response");

            final String responseCode = responseObj.getString("responseCode");

            final boolean exists = jsonObject.getString("exists").equals("true");

            if (responseCode.equals("200") && exists) {

                final String user_id = jsonObject.getString("user_id");
                Log.d(TAG, "User_ID -> "+user_id);
                mySharedPrefs.setUserID(user_id);
               //  If User logs out and Adds some new expenses and then logs back in again.
                syncUserDataToDB();
                ImportDataFromServerDB();

            } else if (responseCode.equals("404") && !exists) {

                storeUserInDB(user);

            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

//        Call<VerifyUserResult> call = NetworkHandler.getNetworkHandler(getContext()).getNetworkApi().verifyUser(u_email);
//
//        call.enqueue(new Callback<VerifyUserResult>() {
//            @Override
//            public void onResponse(Call<VerifyUserResult> call, Response<VerifyUserResult> response) {
//                Log.d(TAG, "onResponse: call" + response.isSuccessful());
//
//                boolean exists = response.body().userExists();
//                final String u_id = response.body().getUser_id();
//                ResponseResult result = response.body().getResponseResult();
//
//                final String responseCode = result.getResponseCode();
//
//                if (responseCode.equalsIgnoreCase("200") && exists) {
//
//                    Log.d(TAG, "Verification Result :: User_ID -> "+u_id);
//                    mySharedPrefs.setUserID(u_id);
//                    // If User logs out and Adds some new expenses and then logs back in again.
//                    syncUserDataToDB();
//                    ImportDataFromServerDB();
//
//                } else if (responseCode.equalsIgnoreCase("404") && !exists) {
//                    storeUserInDB(user);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<VerifyUserResult> call, Throwable t) {
//
//                Log.d(TAG, "Failed Somehow");
//
//            }
//        });

    }

    private void calculateMonthlyExpense() {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month_no);
        calendar.set(Calendar.DATE, 1);

        List<String> startEndDate = utilities.getMonthStartEndDate(calendar, false);
        Log.d(TAG, "Start Date : "+startEndDate.get(0)+", End Date : "+startEndDate.get(1));

        Cursor res = dbHelper.fetchExpensesTotAmt__BetweenDates(startEndDate.get(0), startEndDate.get(1));
        res.moveToNext();

        String month_expense = res.getString(0);
        if (month_expense == null)
            month_expense = "0";

        tv_total_month_amt.setText(month_expense);

    }

    private void storeUserInDB(FirebaseUser user) {

        final String user_name = user.getDisplayName();
        final String user_email = user.getEmail();
        final String user_pass = "";

        try {

            final String res = new BgWorker(getContext()).execute("registerUser", user_name, user_email, user_pass).get();
            Log.d(TAG, "Register Result : "+res);

            final JSONObject jsonObject = new JSONObject(res.trim());

            final JSONObject responseObj = jsonObject.getJSONObject("response");

            final String responseCode = responseObj.getString("responseCode");

            if (responseCode.equals("200")) {

                final JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                final String user_id = jsonObject1.getString("User_ID");
                Log.d(TAG, "User_ID -> "+user_id);

                mySharedPrefs.setUserID(user_id);

                syncUserDataToDB();

            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

//        RequestBody f_name = RequestBody.create(user_name, MediaType.parse("text/plain"));
//        RequestBody pass = RequestBody.create(user_pass, MediaType.parse("text/plain"));
//        RequestBody u_email = RequestBody.create(user_email, MediaType.parse("text/plain"));
//
//        Call<RegisterUser> call = NetworkHandler.getNetworkHandler(getContext()).getNetworkApi().registerUser(f_name, u_email, pass);
//
//        call.enqueue(new Callback<RegisterUser>() {
//            @Override
//            public void onResponse(Call<RegisterUser> call, Response<RegisterUser> response) {
//                Log.d(TAG, "onResponse: call" + response.isSuccessful());
//
//                RegisterResult registerResult = response.body().getResult();
//                ResponseResult result = response.body().getResponseResult();
//
//                final String responseCode = result.getResponseCode();
//
//                if (responseCode.equalsIgnoreCase("200")) {
//
//                    Log.d(TAG, "Register Result :: User_ID -> "+registerResult.getUser_id());
//                    mySharedPrefs.setUserID(registerResult.getUser_id());
//
//                    syncUserDataToDB();
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<RegisterUser> call, Throwable t) {
//
//                Log.d(TAG, "Failed Somehow");
//
//            }
//        });

    }

    private void syncUserDataToDB() {

        syncProgressDialog.show();
        utilities.syncExpenseTableData(TAG, syncProgressDialog, false, false, false, true, false);

        utilities.syncExpenseTypeData(TAG, syncProgressDialog, false, false, true, "false", false);
        syncProgressDialog.hide();

        utilities.showBottomSnackBar(getContext(), coordinatorLayout, getResources().getString(R.string.util_data_sync_successful),
                R.color.internet_status_color);

    }

    private void setup() {

        final Resources resources = getResources();

        monthsList.add(resources.getText(R.string.pf_jan).toString());
        monthsList.add(resources.getText(R.string.pf_feb).toString());
        monthsList.add(resources.getText(R.string.pf_mar).toString());
        monthsList.add(resources.getText(R.string.pf_apr).toString());
        monthsList.add(resources.getText(R.string.pf_may).toString());
        monthsList.add(resources.getText(R.string.pf_jun).toString());
        monthsList.add(resources.getText(R.string.pf_jul).toString());
        monthsList.add(resources.getText(R.string.pf_aug).toString());
        monthsList.add(resources.getText(R.string.pf_sep).toString());
        monthsList.add(resources.getText(R.string.pf_oct).toString());
        monthsList.add(resources.getText(R.string.pf_nov).toString());
        monthsList.add(resources.getText(R.string.pf_dec).toString());

        final Calendar calendar = Calendar.getInstance();
        month_no = calendar.get(Calendar.MONTH);
        String month_name = monthsList.get(month_no);
        tv_month_name.setText(month_name);

        final String lang_code = mySharedPreferences.getStringParams(SharedPrefsConstants.USER_SELECTED_LANGUAGE_CODE,
                SharedPrefsConstants.DEFAULT_LANGUAGE_CODE);
        if (lang_code.equals("en"))
            month_name += "'s";

        tv_total_amt_month_name.setText(month_name);

        if (month_no == 0) {
            im_month_back.setVisibility(View.INVISIBLE);
        }
        calculateWeeklyExpenseData();

    }

    private void calculateWeeklyExpenseData() {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month_no);
        final int maxWeek_no = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        Log.d(TAG, "Max Weeks in Month : "+month_no+" -> "+maxWeek_no);

        List<Entry> totExpEntry = new ArrayList<>();
        final List<String> weeks = new ArrayList<>();
        weeks.add("");


        for (int week_no = 1; week_no <= maxWeek_no; week_no ++) {
            final float weeksExpense = Float.parseFloat(fetchWeeklyExpenseData(week_no));
            totExpEntry.add(new Entry(week_no, weeksExpense));
            weeks.add("Week " + week_no);
        }

        Log.d(TAG, "Bar Entries : "+totExpEntry.toString());
        Log.d(TAG, "Week Data : "+weeks.toString());

        LineDataSet set = new LineDataSet(totExpEntry, "Weekly Expenses");
        set.setValueTextColor(getContext().getResources().getColor(R.color.text_color_2));
        set.setLineWidth(3f);
        set.setCircleRadius(5f);
        set.setCircleHoleRadius(3f);
        set.setCircleHoleColor(getContext().getResources().getColor(R.color.activity_bg));
        set.setColor(getContext().getResources().getColor(R.color.pink_400));
        set.setValueTextSize(12f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(set);
        total_expenses_chart.setData(data);
        total_expenses_chart.getLegend().setEnabled(false);
        total_expenses_chart.invalidate();

        XAxis xAxis = total_expenses_chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));


    }

    private String fetchWeeklyExpenseData(int week_no) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, month_no);
        calendar.set(Calendar.WEEK_OF_MONTH, week_no);
        final Date monday = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        final Date sunday = calendar.getTime();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        final String sDate = sdf.format(monday);
        final String eDate = sdf.format(sunday);

        Cursor res = dbHelper.fetchExpensesTotAmt__BetweenDates(sDate, eDate);
        res.moveToNext();

        String weeklyExpense = res.getString(0);
        if (weeklyExpense == null)
            weeklyExpense = "0";

        return weeklyExpense;

    }

    private void setUserDetails(FirebaseUser firebaseUser) {

        final String user_name = firebaseUser.getDisplayName();
        final Uri photoUri = firebaseUser.getPhotoUrl();

        tv_user_name.setText(user_name);

        Glide.with(getContext())
                .load(photoUri)
                .into(im_profile_pic);

    }

    private void ImportDataFromServerDB() {

        importProgressDialog.show();
        final String u_id = mySharedPrefs.getUserID();
//        RequestBody user_id = RequestBody.create(u_id, MediaType.parse("text/plain"));

        try {

            final String res = new BgWorker(getContext()).execute("ImportDataFromServerDB", u_id).get();
            Log.d(TAG, "ImportDataFromServerDB Response : "+res);

            final JSONObject jObj = new JSONObject(res.trim());

            final JSONObject responseObj = jObj.getJSONObject("response");

            final String responseCode = responseObj.getString("responseCode");

            if (responseCode.equals("200")) {

                final JSONArray expListJsonArray = jObj.getJSONArray("expenses_list");

                if (expListJsonArray.length() > 0) {

                    if (dbHelper.ExpenseTableHasData()) {
                        dbHelper.deleteAllRows__ExpenseTable();
                    }

                    for (int index = 0; index < expListJsonArray.length(); index ++) {

                        final JSONArray expJsonArray = expListJsonArray.getJSONArray(index);

                        final List<String> expense = new ArrayList<>();
                        expense.add(expJsonArray.getString(0));
                        expense.add(expJsonArray.getString(1));
                        expense.add(expJsonArray.getString(2));
                        expense.add(expJsonArray.getString(3));
                        expense.add(expJsonArray.getString(4));
                        expense.add(expJsonArray.getString(5));
                        expense.add(expJsonArray.getString(6));

                        dbHelper.storeExpense(expense, true);

                    }

                }

                final JSONArray expTypeListJsonArray = jObj.getJSONArray("expense_types_list");

                if (expTypeListJsonArray.length() > 0) {

                    dbHelper.deleteAllRows__ExpenseTypesTable();

                    for (int index = 0; index < expTypeListJsonArray.length(); index ++) {

                        final JSONArray expTypeJsonArray = expTypeListJsonArray.getJSONArray(index);

                        final List<String> expenseType = new ArrayList<>();
                        expenseType.add(expTypeJsonArray.getString(0));
                        expenseType.add(expTypeJsonArray.getString(1));
                        expenseType.add(expTypeJsonArray.getString(2));

                        dbHelper.storeExpenseTypes(expenseType.get(0), expenseType.get(1), expenseType.get(2), true);

                    }

                }

                importProgressDialog.hide();
                utilities.showBottomSnackBar(getContext(), coordinatorLayout, getResources().getString(R.string.util_data_sync_successful),
                        R.color.internet_status_color);

                initLineChart();
                calculateMonthlyExpense();
                calculateWeeklyExpenseData();

            } else if (responseCode.equals("404")) {

                importProgressDialog.hide();
                utilities.showBottomSnackBar(getContext(), coordinatorLayout, getResources().getString(R.string.util_no_data_uploaded_yet),
                        R.color.internet_status_color);

            }

            importProgressDialog.hide();

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

//        Call<ImportDataResult> call = NetworkHandler.getNetworkHandler(getContext()).getNetworkApi().importData(user_id);
//        call.enqueue(new Callback<ImportDataResult>() {
//            @Override
//            public void onResponse(Call<ImportDataResult> call, Response<ImportDataResult> response) {
//                Log.d(TAG, "onResponse: call" + response.isSuccessful());
//
//                List<List<String>> expensesList = response.body().getExpenses_list();
//                List<List<String>> expensesTypesList = response.body().getExpense_Types_list();
//                ResponseResult result = response.body().getResponseResult();
//
//                final String responseCode = result.getResponseCode();
//
//                if (responseCode.equalsIgnoreCase("200")) {
//
//                    if (expensesList.size() > 0) {
//
//                        if (dbHelper.ExpenseTableHasData()) {
//                            dbHelper.deleteAllRows__ExpenseTable();
//                        }
//
//                        ListIterator<List<String>> iter = expensesList.listIterator();
//
//                        while(iter.hasNext()) {
//                            final List<String> expense = iter.next();
//                            dbHelper.storeExpense(expense, true);
//                        }
//
//                    } else {
//                        Log.d(TAG, "Expenses List is Empty");
//                    }
//
//                    if (expensesTypesList.size() > 0) {
//
//                        dbHelper.deleteAllRows__ExpenseTypesTable();
//
//                        ListIterator<List<String>> iter1 = expensesTypesList.listIterator();
//
//                        while(iter1.hasNext()) {
//                            final List<String> expenseType = iter1.next();
//                            dbHelper.storeExpenseTypes(expenseType.get(0), expenseType.get(1), expenseType.get(2), true);
//                        }
//
//                    } else {
//
//                        Log.d(TAG, "ExpensesTypes List is Empty");
//                    }
//
//                    importProgressDialog.hide();
//                    utilities.showBottomSnackBar(getContext(), coordinatorLayout, "Data Imported Successfully!", R.color.internet_status_color);
//
//                    initLineChart();
//                    calculateMonthlyExpense();
//                    calculateWeeklyExpenseData();
//
//                } else if (responseCode.equalsIgnoreCase("404")) {
//                    importProgressDialog.hide();
//                    utilities.showBottomSnackBar(getContext(), coordinatorLayout, "No Data uploaded yet from this Account!", R.color.internet_status_color);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ImportDataResult> call, Throwable t) {
//
//                importProgressDialog.hide();
//                Log.d(TAG, "Failed Somehow");
//            }
//        });

    }

    private void logoutUser() {

        final String loginMethod = mySharedPrefs.getLoginMethod();

        if (loginMethod.equals("Google")) {

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getResources().getString(R.string.pf_logout_adb_header))
                    .setMessage(getResources().getString(R.string.pf_logout_adb_desc))
                    .setPositiveButton(getResources().getString(R.string.ea_positive_btn_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Signing Out from the google API, so that user can select different acc. during,
                            // signIn with google.
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

                            GoogleSignInClient gsClient = GoogleSignIn.getClient(getContext(), gso);

                            gsClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseAuth.getInstance().signOut();

                                        mySharedPrefs.setLoggedIn(false);
                                        mySharedPrefs.setLoginMethod("");
                                        mySharedPrefs.setUserID("");

                                        ll_google_signIn.setVisibility(View.VISIBLE);
                                        tv_user_name.setText("Welcome");
                                        im_profile_pic.setImageResource(R.drawable.ic_profile_selected);
//                                        ll_sync_db.setVisibility(View.GONE);
//                                        ll_import.setVisibility(View.GONE);
                                        ll_logout.setVisibility(View.GONE);

                                    }
                                }
                            });

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.ea_negative_btn_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }

    }

}
