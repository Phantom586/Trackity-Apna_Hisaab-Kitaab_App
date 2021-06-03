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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseTypeAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.WeekAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Expenses;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Week;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.AddExpenseBottomSheet;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.ManageExpenseTypes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView, expenseRecyclerView, weekRecyclerView;
    private TextView tv_add_expense_header, tv_today_total_expense;
    private List<ExpenseType> expenseTypeList, exp_heads_list;
    private static String expHeadID = "", expenseType = "All";
    private AddExpenseBottomSheet addExpenseBottomSheet;
    private MySharedPreferences mySharedPreferences;
    private List<List<String>> exp_sub_heads_list;
    private ExpenseTypeAdapter expenseTypeAdapter;
    private final String TAG = "HomeFragment";
    private FloatingActionButton addExpense;
    private RelativeLayout noExpensesLayout;
    private ImageView im_manage_exp_types;
    private ExpenseAdapter expenseAdapter;
    private SimpleDateFormat date_format;
    private List<Expenses> expensesList;
    private boolean isOnline = false;
    private WeekAdapter weekAdapter;
    private static Date dateObject;
    private List<Week> weekList;
    private Utilities utilities;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.fh_expense_type_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        expenseRecyclerView = view.findViewById(R.id.fh_expense_recyclerView);
        expenseRecyclerView.setHasFixedSize(true);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        weekRecyclerView = view.findViewById(R.id.fh_week_recyclerView);
        weekRecyclerView.setHasFixedSize(true);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Tovuti.from(getContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                isOnline = isConnected;
            }
        });

        date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        tv_today_total_expense = view.findViewById(R.id.fh_today_total_expense);
        tv_add_expense_header =view.findViewById(R.id.fh_add_expense_header);
        im_manage_exp_types = view.findViewById(R.id.fh_manage_exp_types);
        mySharedPreferences = new MySharedPreferences(getContext());
        noExpensesLayout = view.findViewById(R.id.fh_no_expenses);
        addExpense = view.findViewById(R.id.fh_add_expense);
        utilities = new Utilities(getContext());
        exp_sub_heads_list = new ArrayList<>();
        dbHelper = new DBHelper(getContext());
        expenseTypeList = new ArrayList<>();
        exp_heads_list = new ArrayList<>();
        expensesList = new ArrayList<>();
        weekList = new ArrayList<>();

        expenseAdapter = new ExpenseAdapter(getContext(), expensesList, false, "EA");
        expenseRecyclerView.setAdapter(expenseAdapter);
        expenseAdapter.setOnItemClickListener(new ExpenseAdapter.onItemClickListener() {
            @Override
            public void onDeleteExpense(String expId, int position) {
                deleteExpense(expId, position);
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exp_heads_list.size() > 0)
                    exp_heads_list.clear();
                fetchAEBSData();

                addExpenseBottomSheet = new AddExpenseBottomSheet(exp_heads_list, exp_sub_heads_list);
                addExpenseBottomSheet.setOnItemClickListener(new AddExpenseBottomSheet.onItemClickListener() {
                    @Override
                    public void onExpenseAdded() {
                        addExpenseBottomSheet.dismiss();

                        final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                        if (isOnline && isLoggedIn)
                            utilities.syncExpenseTableData(TAG,  null, true, false, false, false, true);

                        fetchExpenses();
                        fetchTodayTotalExpense();
                    }
                });
                addExpenseBottomSheet.show(requireFragmentManager(), addExpenseBottomSheet.getTag());
            }
        });

        im_manage_exp_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(), ManageExpenseTypes.class);
                in.putExtra("comingFrom", "HomeFragment");
                v.getContext().startActivity(in);
            }
        });

        storeExpenseTypes();

//        final boolean isUpdateDone = mySharedPreferences.isUpdateDone();
//        if (!isUpdateDone)
//            storeExpHeadUpdates();

        fetchExpense_Types();

        fetchExpense_TypesBS();

        fetch_week_data();

        fetchExpenses();

        fetchTodayTotalExpense();

        return view;

    }

    private void deleteExpense(String expID, int position) {

        dbHelper.deleteExpense__byID(expID);
        dbHelper.storeDeletedItem(expID, "Expenses");

        final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
        if (isOnline && isLoggedIn)
            utilities.syncExpenseTableData(TAG, null, false, false, true, false, true);

        expensesList.remove(position);
        expenseAdapter.notifyDataSetChanged();
        fetchTodayTotalExpense();

    }

//    private void storeExpHeadUpdates() {

//        final String expHeadUpdateList = "{\n" +
//                "  \"Updates\": {\n" +
//                "    \"Heads\": [\n" +
//                "      \"My Pet\",\n" +
//                "      \"Testing 1\"\n" +
//                "    ],\n" +
//                "    \"SubHeads\": [\n" +
//                "      [\"Medicine\", \"Nutrition\", \"Accessories\", \"Grooming\"],\n" +
//                "      []\n" +
//                "    ]\n" +
//                "  }\n" +
//                "}";
//
//        try {
//            final JSONObject jsonObject = new JSONObject(expHeadUpdateList);
//
//            final JSONObject jsonObject1 = jsonObject.getJSONObject("Updates");
//            final JSONArray jsonArray = jsonObject1.getJSONArray("Heads");
//            final JSONArray jsonArray1 = jsonObject1.getJSONArray("SubHeads");
//
//            for (int index = 0; index < jsonArray.length(); index ++) {
//
//                final String newHead = jsonArray.getString(index);
//
//                final String expHeadID = dbHelper.storeNewExpHeads(newHead);
//
//                final JSONArray jsonArray2 = jsonArray1.getJSONArray(index);
//
//                for (int sIndex = 0; sIndex < jsonArray2.length(); sIndex ++) {
//
//                    final String newSubHead = jsonArray2.getString(sIndex);
//
//                    dbHelper.storeNewExpSubHeads(expHeadID, newSubHead);
//
//                }
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        mySharedPreferences.setUpdateDone(true);

//    }

    private void storeExpenseTypes() {

        final boolean expTypesStored = mySharedPreferences.getExpTypesStored();
        if (!expTypesStored) {
            Log.d(TAG, "Expense Type Not Stored");
            final String expTypeList = "{\n" +
                    "  \"Expense_Heads\" : [\n" +
                    "    [\"1\", \"0\", \"Expenses\"],\n" +
                    "    [\"2\", \"0\", \"Income\"],\n" +
                    "    [\"3\", \"0\", \"My Pet\"],\n" +
                    "    [\"4\", \"1\", \"Kirana\"],\n" +
                    "    [\"5\", \"1\", \"Milk\"],\n" +
                    "    [\"6\", \"1\", \"Education\"],\n" +
                    "    [\"7\", \"1\", \"Medicine\"],\n" +
                    "    [\"8\", \"1\", \"Garments\"],\n" +
                    "    [\"9\", \"1\", \"Laon Payment\"],\n" +
                    "    [\"10\", \"1\", \"Investment\"],\n" +
                    "    [\"11\", \"1\", \"Electricity/Mobile\"],\n" +
                    "    [\"12\", \"1\", \"Vegetables/Fruits\"],\n" +
                    "    [\"13\", \"1\", \"House Rent\"],\n" +
                    "    [\"14\", \"1\", \"Dish Expense\"],\n" +
                    "    [\"15\", \"1\", \"Others\"],\n" +
                    "    [\"16\", \"2\", \"Salary\"],\n" +
                    "    [\"17\", \"2\", \"Rent\"],\n" +
                    "    [\"18\", \"2\", \"Interest\"],\n" +
                    "    [\"19\", \"2\", \"Commission\"],\n" +
                    "    [\"20\", \"2\", \"Other\"],\n" +
                    "    [\"21\", \"3\", \"Medicine\"],\n" +
                    "    [\"22\", \"3\", \"Nutrition\"],\n" +
                    "    [\"23\", \"3\", \"Accessories\"],\n" +
                    "    [\"24\", \"3\", \"Grooming\"]\n" +
                    "  ]\n" +
                    "}";
            try {
                final JSONObject jsonObject = new JSONObject(expTypeList);
                final JSONArray jsonArray = jsonObject.getJSONArray("Expense_Heads");

                for (int index = 0; index < jsonArray.length(); index++) {

                    final JSONArray jsonArray1 = jsonArray.getJSONArray(index);

                    dbHelper.storeExpenseTypes(jsonArray1.getString(0), jsonArray1.getString(1), jsonArray1.getString(2), false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mySharedPreferences.setExpTypesStored(true);
        }

    }

    private void fetch_week_data() {

        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH));

        final Calendar cal = Calendar.getInstance();
        final Date today_date = cal.getTime();

        dateObject = today_date;

        final SimpleDateFormat weekDate = new SimpleDateFormat("d", Locale.ENGLISH);
        final SimpleDateFormat weekDay = new SimpleDateFormat("E", Locale.ENGLISH);

        for (int dIndex = 0; dIndex < 7; dIndex++) {

            if (dIndex == 0)
                calendar.add(Calendar.DATE, 0);
            else
                calendar.add(Calendar.DATE, 1);

            final Date date = calendar.getTime();

            weekList.add(
                    new Week(
                            weekDay.format(date),
                            weekDate.format(date),
                            date.equals(today_date),
                            date
                    )
            );

        }

        weekAdapter = new WeekAdapter(getContext(), weekList);
        weekRecyclerView.setAdapter(weekAdapter);
        weekAdapter.setOnItemClickListener(new WeekAdapter.onItemClickListener() {
            @Override
            public void onWeekClicked(Date date) {
                Log.d(TAG, "Selected Date : "+date.toString());
                dateObject = date;
                fetchExpenses();
                fetchTodayTotalExpense();
            }
        });

    }

    private void fetchExpenses() {

        final String current_date = date_format.format(dateObject);
        final String sDate = current_date + " 00:00:00";
        final String eDate = current_date + " 23:59:00";

        Log.d(TAG, "Fetching Expenses for Exp_Head_ID : "+expHeadID+", Date : "+current_date);

        Cursor res;

        res = dbHelper.fetchExpenses(expHeadID, expenseType, sDate, eDate);

        if (res.getCount() == 0) {
            noExpensesLayout.setVisibility(View.VISIBLE);
            if (expensesList.size() > 0) {
                expensesList.clear();
                expenseAdapter.notifyDataSetChanged();
            }
        } else {

            noExpensesLayout.setVisibility(View.GONE);
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

    }

    private void fetchTodayTotalExpense() {

        final String current_date = date_format.format(dateObject);
        final String sDate = current_date + " 00:00:00";
        final String eDate = current_date + " 23:59:00";

        Cursor res = dbHelper.fetchTodayTotalExpense(expHeadID, expenseType, sDate, eDate);
        res.moveToFirst();

        String totalAmt = res.getString(0);
        if (totalAmt == null)
            totalAmt = "0";
        Log.d(TAG, "Today's Total Expense Amount : "+totalAmt);

        tv_today_total_expense.setText(totalAmt);

    }

    private void fetchAEBSData() {

        final Cursor res = dbHelper.fetchExpenseTypes(true);

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

    }

    private void fetchExpense_Types() {

        final Cursor res = dbHelper.fetchExpenseTypes(true);

        // Adding the default 'All' expenseType;
        expenseTypeList.add(
                new ExpenseType(
                        "0",
                        "0",
                        "All",
                        true
                )
        );

        while(res.moveToNext()) {

            expenseTypeList.add(
                    new ExpenseType(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(0).equals("0")
                    )
            );

        }

        res.close();

        expenseTypeAdapter = new ExpenseTypeAdapter(getContext(), expenseTypeList, 0);
        recyclerView.setAdapter(expenseTypeAdapter);

        recyclerView.setVisibility(View.VISIBLE);

        expenseTypeAdapter.setOnItemClickListener(new ExpenseTypeAdapter.onItemClickListener() {
            @Override
            public void onExpenseTypeSelect(String id, String expType) {

                /* Fetch Expenses based on the ExpenseType selected */
                Log.d(TAG, expType + " Selected");
                expHeadID = id;
                expenseType = expType;
                tv_add_expense_header.setText(R.string.fh_card_header);
                fetchExpenses();
                fetchTodayTotalExpense();

            }
        });

    }

    private void fetchExpense_TypesBS() {

        final Cursor res = dbHelper.fetchExpenseTypes(false);

        while(res.moveToNext()) {

            final List<String> temp = new ArrayList<>();
            temp.add(res.getString(0));
            temp.add(res.getString(1));
            temp.add(res.getString(2));
            exp_sub_heads_list.add(temp);

        }

    }

}
