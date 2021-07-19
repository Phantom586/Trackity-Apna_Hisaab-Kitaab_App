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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ManageExpHeadAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ManageExpSubHeadAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.MySharedPreferences;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ManageExpenseTypes extends AppCompatActivity {

    private RecyclerView exp_head_recyclerView, exp_subHead_recyclerView;
    private static String exp_h_id = "", exp_sh_id = "", comingFrom;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private ManageExpSubHeadAdapter manageExpSubHeadAdapter;
    private ImageView im_add_exp_head, im_add_exp_sub_head;
    private List<ExpenseType> exp_heads_list, exp_sh_list;
    private ManageExpHeadAdapter manageExpHeadAdapter;
    private MySharedPreferences mySharedPreferences;
    private List<List<String>> exp_sub_heads_list;
    private CoordinatorLayout coordinatorLayout;
    private static boolean expTypeFlag = false;
    private final String TAG = "ManageExpType";
    private TextInputLayout til_exp_type_name;
    private List<ExpenseType> expenseTypeList;
    private View customExpenseHeadView;
    private EditText et_exp_type_name;
    private boolean isOnline = false;
    private Utilities utilities;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_expense_types);

        exp_head_recyclerView = findViewById(R.id.met_exp_head_recyclerView);
        exp_head_recyclerView.setHasFixedSize(false);
        exp_head_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        exp_subHead_recyclerView = findViewById(R.id.met_exp_sub_head_recyclerView);
        exp_subHead_recyclerView.setHasFixedSize(true);
        exp_subHead_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        im_add_exp_sub_head = findViewById(R.id.met_add_exp_sub_head);
        coordinatorLayout = findViewById(R.id.met_coordinator_layout);
        mySharedPreferences = new MySharedPreferences(this);
        im_add_exp_head = findViewById(R.id.met_add_exp_head);
        exp_sub_heads_list = new ArrayList<>();
        dbHelper = new DBHelper(this);
        expenseTypeList = new ArrayList<>();
        utilities = new Utilities(this);
        exp_heads_list = new ArrayList<>();
        exp_sh_list = new ArrayList<>();

        Intent in = getIntent();
        comingFrom = in.getStringExtra("comingFrom");

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                isOnline = isConnected;
            }
        });

        im_add_exp_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customExpenseHeadView = LayoutInflater.from(v.getContext()).inflate(R.layout.custom_add_exp_type_dialog, null, false);
                launchAddExpHeadDialog(v.getContext().getResources().getString(R.string.met_create_exp_head), "Head");
            }
        });

        im_add_exp_sub_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customExpenseHeadView = LayoutInflater.from(v.getContext()).inflate(R.layout.custom_add_exp_type_dialog, null, false);
                launchAddExpHeadDialog(v.getContext().getResources().getString(R.string.met_create_exp_sub_head), "Sub-Head");
            }
        });

        fetchExpenseHeads();

        fetchExpenseSubHeads();

        displayExpenseHeaders();

        manageExpSubHeadAdapter = new ManageExpSubHeadAdapter(this, exp_sh_list);
        exp_subHead_recyclerView.setAdapter(manageExpSubHeadAdapter);

        manageExpSubHeadAdapter.setOnItemClickListener(new ManageExpSubHeadAdapter.onItemClickListener() {
            @Override
            public void onExpenseSubHeadSelect(String id, String p_id, String expType) {
                exp_h_id = p_id;
                exp_sh_id = id;
            }

            @Override
            public void onExpenseDelete(int position, String id) {

                new MaterialAlertDialogBuilder(ManageExpenseTypes.this)
                        .setTitle(getResources().getString(R.string.met_del_sub_header))
                        .setMessage(getResources().getString(R.string.met_del_sub_desc))
                        .setPositiveButton(getResources().getString(R.string.ea_positive_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                exp_sh_list.remove(position);
                                manageExpSubHeadAdapter.notifyItemRemoved(position);
                                dbHelper.deleteExpenseType__byID(id, false);
                                dbHelper.storeDeletedItem(id, "Expense_Types");

                                final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                                if (isOnline && isLoggedIn)
                                    utilities.syncExpenseTypeData(TAG, null, false, true, false, "false", true);

                                exp_sub_heads_list.clear();
                                fetchExpenseSubHeads();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.ea_negative_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

    }

    private void launchAddExpHeadDialog(String title, String headType) {

        til_exp_type_name = customExpenseHeadView.findViewById(R.id.caet_exp_type_name);
        et_exp_type_name = customExpenseHeadView.findViewById(R.id.caet_exp_t_n);
        et_exp_type_name.requestFocus();

        materialAlertDialogBuilder.setView(customExpenseHeadView)
                .setTitle(title)
                .setPositiveButton(getResources().getString(R.string.met_adb_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String exp_type_name = et_exp_type_name.getText().toString();

                        boolean validated = true;

                        if (exp_type_name.length() == 0) {
                            validated = false;
                            til_exp_type_name.setError("Expense Type Name Couldn't be Empty!");
                        }

                        if (validated) {
                            if (headType.equals("Head")) {
                                dbHelper.storeExpenseTypes("", "0", exp_type_name, false);

                                exp_heads_list.clear();
                                fetchExpenseHeads();
                                displayExpenseHeaders();
                                utilities.showTopSnackBar(getApplicationContext(), coordinatorLayout, "Added Expense Header", R.color.internet_status_color);
                            }
                            else if (headType.equals("Sub-Head")) {

                                if (!expTypeFlag) {
                                    utilities.showTopSnackBar(getApplicationContext(), coordinatorLayout, "Please Choose a Expense Sub-Head!", R.color.pink_400);
                                } else {
                                    Log.d(TAG, "Exp head ID : "+exp_h_id);
                                    dbHelper.storeExpenseTypes("", exp_h_id, exp_type_name, false);

                                    exp_sub_heads_list.clear();
                                    fetchExpenseSubHeads();
                                    filterExpenseSubHeadsByParentID(exp_h_id);
                                    utilities.showTopSnackBar(getApplicationContext(), coordinatorLayout, "Added Expense Sub-Header", R.color.internet_status_color);
                                }

                            }
                            final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                            if (isOnline && isLoggedIn)
                                utilities.syncExpenseTypeData(TAG, null, true, false, false, "false", true);
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.ea_negative_btn_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void fetchExpenseHeads() {

        final Cursor res = dbHelper.fetchExpenseTypes(true);

        while(res.moveToNext()) {

            exp_heads_list.add(
                    new ExpenseType(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            false
                    )
            );

        }

        res.close();

    }

    private void fetchExpenseSubHeads() {

        final Cursor res = dbHelper.fetchExpenseTypes(false);

        while(res.moveToNext()) {

            final List<String> temp = new ArrayList<>();
            temp.add(res.getString(0));
            temp.add(res.getString(1));
            temp.add(res.getString(2));
            exp_sub_heads_list.add(temp);

        }

        res.close();

    }

    private void filterExpenseSubHeadsByParentID(String parent_id) {

        exp_h_id = parent_id;
        expTypeFlag = true;

        if (exp_sh_list.size() > 0)
            exp_sh_list.clear();

        ListIterator<List<String>> iter = exp_sub_heads_list.listIterator();

        while (iter.hasNext()) {
            List<String> dummyList = iter.next();
            if (dummyList.get(1).equals(parent_id)) {

                exp_sh_list.add(
                        new ExpenseType(
                                dummyList.get(0),
                                dummyList.get(1),
                                dummyList.get(2),
                                false
                        )
                );

            }

        }

        manageExpSubHeadAdapter.notifyDataSetChanged();

    }

    private void displayExpenseHeaders() {

        ManageExpHeadAdapter manageExpHeadAdapter = new ManageExpHeadAdapter(this, exp_heads_list);
        exp_head_recyclerView.setAdapter(manageExpHeadAdapter);

        manageExpHeadAdapter.setOnItemClickListener(new ManageExpHeadAdapter.onItemClickListener() {
            @Override
            public void onExpenseTypeSelect(String id, String expType) {

                filterExpenseSubHeadsByParentID(id);

            }

            @Override
            public void onExpenseDelete(int position, String id) {

                new MaterialAlertDialogBuilder(ManageExpenseTypes.this)
                        .setTitle(getResources().getString(R.string.met_del_header))
                        .setMessage(getResources().getString(R.string.met_del_desc))
                        .setPositiveButton(getResources().getString(R.string.ea_positive_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                exp_heads_list.remove(position);
                                dbHelper.deleteExpenseType__byID(id, true);
                                dbHelper.storeDeletedItem(id, "Expense_Types");

                                final boolean isLoggedIn = mySharedPreferences.isLoggedIn();
                                if (isOnline && isLoggedIn)
                                    utilities.syncExpenseTypeData(TAG, null, false, true, false, "false", true);

                                manageExpHeadAdapter.notifyItemRemoved(position);

                                // clearing the sub_head_list of the deleted head.
                                exp_sh_list.clear();
                                manageExpSubHeadAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.ea_negative_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

    }

    public void goBack(View view) {

        Intent in = new Intent(this, MainActivity.class);
        if (comingFrom.equals("ProfileFragment"))
            in.putExtra("fragment_name", "profile");
        else if (comingFrom.equals("HomeFragment"))
            in.putExtra("fragment_name", "home");
        startActivity(in);

    }

    @Override
    public void onBackPressed() {

        Intent in = new Intent(this, MainActivity.class);
        if (comingFrom.equals("ProfileFragment"))
            in.putExtra("fragment_name", "profile");
        else if (comingFrom.equals("HomeFragment"))
            in.putExtra("fragment_name", "home");
        startActivity(in);

    }

}