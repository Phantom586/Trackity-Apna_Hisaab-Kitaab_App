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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Expenses;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.google.android.material.datepicker.MaterialDatePicker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SubHeaderExpenses extends AppCompatActivity {

    private String headID, subHeadID, subHeadName, dateRange, totalAmount;
    private final String TAG = "SubHeaderExpenses";
    private TextView tv_subHeadName, tv_dateRange, tv_totalAmount;
    private ExpenseAdapter expenseAdapter;
    private List<Expenses> expensesList;
    private RecyclerView recyclerView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_header_expenses);

        tv_totalAmount = findViewById(R.id.she_total_exp_amt);
        tv_subHeadName = findViewById(R.id.she_subHeadName);
        tv_dateRange = findViewById(R.id.she_dateRange);
        dbHelper = new DBHelper(this);
        expensesList = new ArrayList<>();

        recyclerView  = findViewById(R.id.she_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent in = getIntent();
        headID = in.getStringExtra("headID");
        dateRange = in.getStringExtra("dateRange");
        subHeadID = in.getStringExtra("subHeadID");
        subHeadName = in.getStringExtra("subHeadName");
        totalAmount = in.getStringExtra("totalAmount");

        tv_subHeadName.setText(subHeadName);
        tv_totalAmount.setText(totalAmount);
        tv_dateRange.setText(dateRange);

        expenseAdapter = new ExpenseAdapter(this, expensesList, true, "SHE");
        recyclerView.setAdapter(expenseAdapter);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.dateRangePicker();

        fetchExpenses();

    }

    private void fetchExpenses() {

        final Cursor res = dbHelper.fetchExpenses__bySubHeadID(headID, subHeadID);

        if (expensesList.size() > 0)
            expensesList.clear();

        while(res.moveToNext()) {

            expensesList.add(
                    new Expenses(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4),
                            res.getString(5),
                            res.getString(6)
                    )
            );

        }

        expenseAdapter.notifyDataSetChanged();

    }

    public void goBack(View view) { super.onBackPressed(); }

}