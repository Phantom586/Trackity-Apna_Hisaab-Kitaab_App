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

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseCalendarAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Expenses;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private ExpenseCalendarAdapter expenseCalendarAdapter;
    private List<String> expenseTypePopUpList;
    private String TAG = "CalendarFragment";
    private SimpleDateFormat dateFormat;
    private List<Expenses> expensesList;
    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private List<EventDay> eventDays;
    private Utilities utilities;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        calendarView = view.findViewById(R.id.fc_calendar);
        expenseTypePopUpList = new ArrayList<>();
        utilities = new Utilities(getContext());
        dbHelper = new DBHelper(getContext());
        expensesList = new ArrayList<>();
        eventDays = new ArrayList<>();

        recyclerView = view.findViewById(R.id.fc_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchExpense_Types();

        expenseCalendarAdapter = new ExpenseCalendarAdapter(getContext(), expensesList, expenseTypePopUpList);
        recyclerView.setAdapter(expenseCalendarAdapter);



        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                final Calendar calendar = eventDay.getCalendar();

                final Date selectedDay = calendar.getTime();

                final String date = dateFormat.format(selectedDay);

                final String sDate = date + " 00:00:00";
                final String eDate = date + " 23:59:00";

                fetchExpenses(sDate, eDate);

            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {

                final Calendar calendar = calendarView.getCurrentPageDate();

                final List<String> startEndDate = utilities.getMonthStartEndDate(calendar, false);

                if (expensesList.size() > 0) {
                    expensesList.clear();
                    expenseCalendarAdapter.notifyDataSetChanged();
                }

                fetchMonthlyEvents(startEndDate.get(0), startEndDate.get(1));

            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {

                final Calendar calendar = calendarView.getCurrentPageDate();

                final List<String> startEndDate = utilities.getMonthStartEndDate(calendar, false);

                if (expensesList.size() > 0) {
                    expensesList.clear();
                    expenseCalendarAdapter.notifyDataSetChanged();
                }

                fetchMonthlyEvents(startEndDate.get(0), startEndDate.get(1));

            }
        });

        fetchTodayExpensesAndMonthlyEvents();

        return view;

    }

    private void fetchTodayExpensesAndMonthlyEvents() {

        // Retrieving the Current Month's Start and End Date.
        final Calendar calendar = Calendar.getInstance();
        final Date currentDate = calendar.getTime();
        final String date = dateFormat.format(currentDate);

        final String sDate = date + " 00:00:00";
        final String eDate = date + " 23:59:00";

        // Calling Func. to fetch Today's Events from the Database.
        fetchExpenses(sDate, eDate);

        // Retrieving the Current Month's Start and End Date.
        final Calendar calendar1 = calendarView.getCurrentPageDate();
        final List<String> startEndDate = utilities.getMonthStartEndDate(calendar1, false);

        Log.d(TAG, "Start Date : "+startEndDate.get(0));

        Log.d(TAG, "End Date : "+startEndDate.get(1));

        fetchMonthlyEvents(startEndDate.get(0), startEndDate.get(1));

    }

    private void fetchMonthlyEvents(String sDate, String eDate) {

        Cursor res = dbHelper.fetchExpenseDates(sDate, eDate);
        Log.d(TAG, "Monthly Events : "+res.toString());

        if (res.getCount() > 0) {

            while (res.moveToNext()) {

                final String dbDate = res.getString(0);
                try {

                    final Date date = dateFormat.parse(dbDate);

                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    eventDays.add(new EventDay(calendar, R.drawable.calendar_event));
                    calendarView.setEvents(eventDays);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }

        res.close();

    }

    private void fetchExpenses(String sDate, String eDate) {

        Cursor res = dbHelper.fetchExpenses("", "All", sDate, eDate);

        if (res.getCount() == 0) {
            if (expensesList.size() > 0) {
                expensesList.clear();
                expenseCalendarAdapter.notifyDataSetChanged();
            }
        } else {

            expensesList.clear();

            while (res.moveToNext()) {

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

            expenseCalendarAdapter.notifyDataSetChanged();

        }

        res.close();

    }

    private void fetchExpense_Types() {

        final Cursor res = dbHelper.fetchExpenseTypes(false);

        while(res.moveToNext()) {

            expenseTypePopUpList.add(res.getString(1));

        }

    }

}
