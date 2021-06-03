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
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ESSubheadAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseTypeAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.SubHeadSummary;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseSummary extends AppCompatActivity {

    private String expHeadID = "", s_date = "", e_date = "", date_range = "", comingFrom;
    private TextView tv_display_date, tv_total_exp_amt, tv_choose_date_range;
    private SimpleDateFormat db_date_format, date_format, disp_date_format;
    private CoordinatorLayout coordinatorLayout, bs_coordinatorLayout;
    private List<ExpenseType> expHeadList, expSubHeadsList;
    private ImageView im_expand_collapse_bottomSheet;
    private RecyclerView rv_exp_head, rv_exp_subHead;
    private BottomSheetBehavior bottomSheetBehavior;
    private ExpenseTypeAdapter expenseTypeAdapter;
    private List<SubHeadSummary> expSHSummaryList;
    private final String TAG = "ExpenseSummary";
    private ESSubheadAdapter esSubheadAdapter;
    private Button btn_search_expenses;
    private LinearLayout linearLayout;
    private Utilities utilities;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        db_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        disp_date_format = new SimpleDateFormat("d MMM", Locale.ENGLISH);
        date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        rv_exp_subHead = findViewById(R.id.aes_exp_subHead_recyclerView);
        rv_exp_subHead.setHasFixedSize(false);
        rv_exp_subHead.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv_exp_subHead.addItemDecoration(itemDecoration);

        rv_exp_head = findViewById(R.id.aes_exp_head_recyclerView);
        rv_exp_head.setHasFixedSize(true);
        rv_exp_head.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        coordinatorLayout = findViewById(R.id.aes_coordinatorLayout);
        expSHSummaryList = new ArrayList<>();
        dbHelper = new DBHelper(this);
        utilities = new Utilities(this);
        expSubHeadsList = new ArrayList<>();
        expHeadList = new ArrayList<>();

        linearLayout = findViewById(R.id.esb_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        im_expand_collapse_bottomSheet = linearLayout.findViewById(R.id.esb_expand_collapse_bs);
        bs_coordinatorLayout = linearLayout.findViewById(R.id.esb_coordinatorLayout);
        tv_choose_date_range = linearLayout.findViewById(R.id.esb_pick_date_range);
        btn_search_expenses = linearLayout.findViewById(R.id.esb_search_btn);
        tv_total_exp_amt = linearLayout.findViewById(R.id.esb_total_exp_amt);
        tv_display_date = linearLayout.findViewById(R.id.esb_display_date);

        Intent in = getIntent();
        comingFrom = in.getStringExtra("comingFrom");

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        im_expand_collapse_bottomSheet.setRotation(90);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        im_expand_collapse_bottomSheet.setRotation(-90);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

            }
        });

        im_expand_collapse_bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (im_expand_collapse_bottomSheet.getRotation() == 90) {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        im_expand_collapse_bottomSheet.setRotation(-90);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                } else if (im_expand_collapse_bottomSheet.getRotation() == -90) {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                        im_expand_collapse_bottomSheet.setRotation(90);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

            }
        });

        tv_choose_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Choose Date Range");

                final MaterialDatePicker<Pair<Long, Long>> date_range_picker = builder.build();

                date_range_picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

                date_range_picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection.first);

                        final Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTimeInMillis(selection.second);

//                        Log.d(TAG, "Selected Date Range is : "+date_range_picker.getHeaderText());
                        tv_display_date.setText(date_range_picker.getHeaderText());
                        date_range = date_range_picker.getHeaderText();

                        final Date date = calendar.getTime();
                        s_date = db_date_format.format(date);

                        final Date date1 = calendar1.getTime();
                        e_date = db_date_format.format(date1);

                        Log.d(TAG, "Start Date : "+s_date+", End Date : "+e_date);

                    }
                });

            }
        });

        btn_search_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expHeadID.equals("")) {

                    utilities.showTopSnackBar(v.getContext(), bs_coordinatorLayout, "Select an Expense Head First!", R.color.internet_status_color);

                }
//                else if (s_date.equals("")  && e_date.equals("")) {
//
//                    utilities.showTopSnackBar(v.getContext(), bs_coordinatorLayout, "Select a Date Range!", R.color.internet_status_color);
//
//                }
                else {
                    fetchCurrentMonthTotalExpenseAmtByHeadID();
                    fetchExpenseSubHeads(expHeadID);
                }
            }
        });

        fetchCurrentMonthStartEndDates();

        esSubheadAdapter = new ESSubheadAdapter(this, expSHSummaryList, coordinatorLayout, date_range);
        rv_exp_subHead.setAdapter(esSubheadAdapter);

        fetchCurrentMonthTotalExpenseAmtByHeadID();

        fetchExpenseHeads();

    }

    private void fetchCurrentMonthStartEndDates() {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);

        List<String> startEndDate = utilities.getMonthStartEndDate(calendar, true);
        s_date = startEndDate.get(0);
        e_date = startEndDate.get(1);

        try {

            String display_date = "";

            Date date = date_format.parse(s_date);
            display_date = disp_date_format.format(date) + " - ";

            date = date_format.parse(startEndDate.get(2));
            display_date += disp_date_format.format(date);

            tv_display_date.setText(display_date);
            date_range = display_date;


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void fetchCurrentMonthTotalExpenseAmtByHeadID() {

        Cursor res = dbHelper.fetchExpensesTotAmt__BetweenDates__byHeadID(s_date, e_date, expHeadID);
        res.moveToNext();

        String month_expense = res.getString(0);
        if (month_expense == null)
            month_expense = "0";

        tv_total_exp_amt.setText(month_expense);

    }

    private void fetchExpenseHeads() {

        final Cursor res = dbHelper.fetchExpenseTypes(true);

        while(res.moveToNext()) {

            expHeadList.add(
                    new ExpenseType(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(0).equals("0")
                    )
            );

        }

        expenseTypeAdapter = new ExpenseTypeAdapter(this, expHeadList, -1);
        rv_exp_head.setAdapter(expenseTypeAdapter);

        expenseTypeAdapter.setOnItemClickListener(new ExpenseTypeAdapter.onItemClickListener() {
            @Override
            public void onExpenseTypeSelect(String id, String expType) {

                expHeadID = id;
                fetchCurrentMonthTotalExpenseAmtByHeadID();
                fetchExpenseSubHeads(id);

            }
        });

    }

    private void fetchExpenseSubHeads(String parent_id) {

        final Cursor res = dbHelper.fetchExpTypes__byID(parent_id);

        if (expSHSummaryList.size() > 0)
            expSHSummaryList.clear();

        if (expSubHeadsList.size() > 0)
            expSubHeadsList.clear();

        while(res.moveToNext()) {

            expSubHeadsList.add(
                    new ExpenseType(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(0).equals("0")
                    )
            );

        }

        for (ExpenseType expenseType : expSubHeadsList) {

            final String total_amount = dbHelper.fetchExpTotalAmt__ByH_IDAndSH_ID(expenseType.getP_id(), expenseType.getId(), s_date, e_date);

            expSHSummaryList.add(
                    new SubHeadSummary(
                            expenseType.getExpenseType(),
                            total_amount,
                            expenseType.getP_id(),
                            expenseType.getId()
                    )
            );

        }

        esSubheadAdapter.notifyDataSetChanged();

    }

    public void goBack(View view) {

        Intent in = new Intent(this, MainActivity.class);
        in.putExtra("fragment_name", "profile");
        startActivity(in);

    }

    @Override
    public void onBackPressed() {

        Intent in = new Intent(this, MainActivity.class);
        in.putExtra("fragment_name", "profile");
        startActivity(in);

    }
}