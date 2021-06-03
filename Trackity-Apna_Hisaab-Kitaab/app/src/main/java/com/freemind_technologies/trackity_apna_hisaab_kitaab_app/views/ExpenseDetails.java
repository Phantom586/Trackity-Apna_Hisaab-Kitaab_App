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

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseSubHeadAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseTypeAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class ExpenseDetails extends AppCompatActivity {

    private RecyclerView exp_heads_recyclerView, exp_sub_heads_recyclerView;
    private SimpleDateFormat db_date_format, date_format, time_format;
    private List<ExpenseType> exp_heads_list, exp_sh_list;
    private ExpenseSubHeadAdapter expenseSubHeadAdapter;
    private static String exp_h_id = "", exp_sh_id = "";
    private TextInputLayout til_exp_desc, til_amount;
    private MySharedPreferences mySharedPreferences;
    private List<List<String>> exp_sub_heads_list;
    private String TAG = "ExpenseDetailsActivity";
    private ExpenseTypeAdapter exp_head_adapter;
    private CoordinatorLayout coordinatorLayout;
    private EditText et_exp_desc, et_exp_amt;
    private TextView tv_date, tv_time;
    private boolean isOnline = false;
    private String expID, comingFrom;
    private Utilities utilities;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        db_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date_format = new SimpleDateFormat("dd, MMM yy", Locale.ENGLISH);
        time_format = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);

        coordinatorLayout = findViewById(R.id.aed_coordinator_layout);
        mySharedPreferences = new MySharedPreferences(this);
        et_exp_amt = findViewById(R.id.aed_et_amount);
        et_exp_desc = findViewById(R.id.aed_et_desc);
        til_amount = findViewById(R.id.aed_amount);
        til_exp_desc = findViewById(R.id.aed_desc);
        exp_sub_heads_list = new ArrayList<>();
        tv_date = findViewById(R.id.aed_date);
        tv_time = findViewById(R.id.aed_time);
        dbHelper = new DBHelper(this);
        utilities = new Utilities(this);
        exp_heads_list = new ArrayList<>();
        exp_sh_list = new ArrayList<>();

        Intent in = getIntent();
        expID = in.getStringExtra("expID");
        exp_h_id = in.getStringExtra("exp_Head_ID");
        comingFrom = in.getStringExtra("comingFrom");
        exp_sh_id = in.getStringExtra("exp_Subhead_ID");

        exp_heads_recyclerView = findViewById(R.id.aed_exp_head_recyclerView);
        exp_heads_recyclerView.setHasFixedSize(true);
        exp_heads_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        exp_sub_heads_recyclerView = findViewById(R.id.aed_exp_sub_head_recyclerView);
        exp_sub_heads_recyclerView.setNestedScrollingEnabled(false);

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                isOnline = isConnected;
            }
        });

        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(this)
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.TOP)
                //whether RecyclerView can scroll. TRUE by default
                .setScrollingEnabled(true)
                //set maximum views count in a particular row
                .setMaxViewsInRow(5)
                //set gravity resolver where you can determine gravity for item in position.
                //This method have priority over previous one
                .setGravityResolver(new IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.LEFT;
                    }
                })
                //you are able to break row due to your conditions. Row breaker should return true for that views
                .setRowBreaker(new IRowBreaker() {
                    @Override
                    public boolean isItemBreakRow(@IntRange(from = 0) int position) {
                        return position == 3 || position == 6 || position == 11;
                    }
                })
                //a layoutOrientation of layout manager, could be VERTICAL OR HORIZONTAL. HORIZONTAL by default
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                // row strategy for views in completed row, could be STRATEGY_DEFAULT, STRATEGY_FILL_VIEW,
                //STRATEGY_FILL_SPACE or STRATEGY_CENTER
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                // whether strategy is applied to last row. FALSE by default
                .withLastRow(true)
                .build();
        exp_sub_heads_recyclerView.setLayoutManager(chipsLayoutManager);

        setupDateTimePickers();

        fetchExpenseTypes();

        displayExpenseHeaders();

        expenseSubHeadAdapter = new ExpenseSubHeadAdapter(this, exp_sh_list);
        exp_sub_heads_recyclerView.setAdapter(expenseSubHeadAdapter);

        expenseSubHeadAdapter.setOnItemClickListener(new ExpenseSubHeadAdapter.onItemClickListener() {
            @Override
            public void onExpenseSubHeadSelect(String id, String p_id, String expType) {
                exp_h_id = p_id;
                exp_sh_id = id;
            }
        });

        showExpenseData();

    }

    private void setupDateTimePickers() {

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Start Date");

                final MaterialDatePicker start_date_picker = builder.build();

                start_date_picker.show(getSupportFragmentManager(), "DATE_PICKER");

                start_date_picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((long)selection);

                        final Date date = calendar.getTime();
                        final String s_date = date_format.format(date);
                        tv_date.setText(s_date);

                    }
                });

            }
        });

        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();

                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");

                materialTimePicker.addOnPositiveButtonClickListener(dialog -> {

                    calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());

                    final Date date = calendar.getTime();
                    final String s_time = time_format.format(date);
                    tv_time.setText(s_time);

                });

            }
        });

    }

    private void fetchExpenseTypes() {

        Cursor res = dbHelper.fetchExpenseTypes(true);

        while(res.moveToNext()) {

            exp_heads_list.add(
                    new ExpenseType(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(0).equals("0")
                    )
            );

        }

        res.close();

        res = dbHelper.fetchExpenseTypes(false);

        while(res.moveToNext()) {

            final List<String> temp = new ArrayList<>();
            temp.add(res.getString(0));
            temp.add(res.getString(1));
            temp.add(res.getString(2));
            exp_sub_heads_list.add(temp);

        }

        res.close();

    }

    private void displayExpenseHeaders() {

        exp_head_adapter = new ExpenseTypeAdapter(this, exp_heads_list, -1);
        exp_heads_recyclerView.setAdapter(exp_head_adapter);

        exp_head_adapter.setOnItemClickListener(new ExpenseTypeAdapter.onItemClickListener() {
            @Override
            public void onExpenseTypeSelect(String id, String expType) {

                if (exp_sh_list.size() > 0)
                    exp_sh_list.clear();

                ListIterator<List<String>> iter = exp_sub_heads_list.listIterator();

                while (iter.hasNext()) {
                    List<String> dummyList = iter.next();
                    if (dummyList.get(1).equals(id)) {

                        exp_sh_list.add(
                                new ExpenseType(
                                        dummyList.get(0),
                                        dummyList.get(1),
                                        dummyList.get(2),
                                        (dummyList.get(0).equals(exp_sh_id) && dummyList.get(1).equals(exp_h_id))
                                )
                        );

                    }

                }

                expenseSubHeadAdapter.notifyDataSetChanged();

            }
        });

    }

    private void showExpenseData() {

        Cursor res = dbHelper.fetchExpenseById(expID);

        if (res.getCount() > 0) {

            res.moveToNext();
            final String date = res.getString(5);
            try {

                final Date db_date = db_date_format.parse(date);
                final String disp_date = date_format.format(db_date);
                tv_date.setText(disp_date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            et_exp_desc.setText(res.getString(3));

            et_exp_amt.setText(res.getString(4));

            tv_time.setText(res.getString(6));

        }

    }

    private boolean isValidated() {

        boolean validated = true;

        final String desc, amount;

        desc = et_exp_desc.getText().toString();
        amount = et_exp_amt.getText().toString();

        if (desc.length() == 0) {
            til_exp_desc.setError("Description can not be blank!");
            validated = false;
        }
        if (amount.length() == 0) {
            til_amount.setError("Amount can not be ₹0!");
            validated = false;
        }

        return validated;

    }

    public void updateExpense(View view) {

        if (isValidated()) {

            final String desc = et_exp_desc.getText().toString();
            final String amount = et_exp_amt.getText().toString();
            final String date = tv_date.getText().toString();
            final String time = tv_time.getText().toString();
            final Date date1;
            String db_date = "";
            try {
                date1 = date_format.parse(date);
                db_date = db_date_format.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final List<String> expense = new ArrayList<>();
            expense.add(exp_h_id);
            expense.add(exp_sh_id);
            expense.add(desc);
            expense.add(amount);
            expense.add(db_date);
            expense.add(time);

            final String dbOp = dbHelper.updateExpense(expID, expense);
            final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
            if (isOnline && isLoggedIn) {
                if (dbOp.equals("update"))
                    utilities.syncExpenseTableData(TAG, null, false, true, false, false, true);
                else if (dbOp.equals("store"))
                    utilities.syncExpenseTableData(TAG, null, true, false, false, false, true);
            }
            utilities.showTopSnackBar(this, coordinatorLayout,"Expense Updated!", R.color.purple_200);
            showExpenseData();

        }

    }

    public void deleteExpense(View view) {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbHelper.deleteExpense__byID(expID);
                        dbHelper.storeDeletedItem(expID, "Expenses");

                        final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                        if (isOnline && isLoggedIn)
                            utilities.syncExpenseTableData(TAG, null, false, false, true, false, true);

                        Intent in = new Intent(ExpenseDetails.this, MainActivity.class);
                        if (comingFrom.equals("EA"))
                            in.putExtra("fragment_name", "home");
                        else if (comingFrom.equals("ECA"))
                            in.putExtra("fragment_name", "calendar");
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void goBack(View view) {

        if (comingFrom.equals("SHE"))
            super.onBackPressed();
        else {
            Intent in = new Intent(this, MainActivity.class);
            if (comingFrom.equals("EA"))
                in.putExtra("fragment_name", "home");
            else if (comingFrom.equals("ECA"))
                in.putExtra("fragment_name", "calendar");
            startActivity(in);
        }

    }

    @Override
    public void onBackPressed() {

        if (comingFrom.equals("SHE"))
            super.onBackPressed();
        else {
            Intent in = new Intent(this, MainActivity.class);
            if (comingFrom.equals("EA"))
                in.putExtra("fragment_name", "home");
            else if (comingFrom.equals("ECA"))
                in.putExtra("fragment_name", "calendar");
            startActivity(in);
        }

    }
}