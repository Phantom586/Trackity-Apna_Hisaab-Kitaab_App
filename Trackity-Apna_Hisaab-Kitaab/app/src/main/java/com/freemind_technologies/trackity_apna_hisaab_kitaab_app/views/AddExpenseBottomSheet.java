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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters.ExpenseTypeAdapter;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class AddExpenseBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView exp_heads_recyclerView, exp_sub_heads_recyclerView;
    private SimpleDateFormat db_date_format, date_format, time_format;
    private AddExpenseBottomSheet.onItemClickListener mListener;
    private SmartMaterialSpinner<String> smartMaterialSpinner;
    private List<ExpenseType> exp_heads_list, exp_sh_list;
    private TextInputLayout til_exp_desc, til_exp_amount;
    private TextView tv_date, tv_time, tv_expense_type;
    private final String TAG = "AddExpenseBSFragment";
    private static String exp_id = "", exp_p_id = "";
    private List<List<String>> exp_sub_heads_list;
    private ExpenseTypeAdapter exp_head_adapter;
    private CoordinatorLayout coordinatorLayout;
    private EditText et_exp_desc, et_amount;
    private LinearLayout ll_expense_type;
    private Button btn_save_expense;
    private Utilities utilities;
    private DBHelper dbHelper;

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }


    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public interface  onItemClickListener{
        void onExpenseAdded();
//        void onClose();
    }

    public void setOnItemClickListener(AddExpenseBottomSheet.onItemClickListener listener) {
        mListener = listener;
    }

    public AddExpenseBottomSheet(List<ExpenseType> exp_h_list, List<List<String>> exp_s_h_list) {
        exp_heads_list = exp_h_list;
        exp_sub_heads_list = exp_s_h_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_add_expense_bottom_sheet, container, false);

        db_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date_format = new SimpleDateFormat("dd, MMM yy", Locale.ENGLISH);
        time_format = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);

        exp_heads_recyclerView = view.findViewById(R.id.aeb_exp_head_recyclerView);
        exp_heads_recyclerView.setHasFixedSize(true);
        exp_heads_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        smartMaterialSpinner = view.findViewById(R.id.aeb_materialSpinner);
        coordinatorLayout = view.findViewById(R.id.aeb_coordinatorLayout);
        btn_save_expense = view.findViewById(R.id.aeb_save_expense);
        til_exp_amount = view.findViewById(R.id.aeb_amount);
        et_exp_desc = view.findViewById(R.id.aeb_et_desc);
        et_amount = view.findViewById(R.id.aeb_et_amount);
        til_exp_desc = view.findViewById(R.id.aeb_desc);
        tv_date = view.findViewById(R.id.aeb_date);
        tv_time = view.findViewById(R.id.aeb_time);
        utilities = new Utilities(getContext());
        dbHelper = new DBHelper(getContext());
        exp_sh_list = new ArrayList<>();

        et_exp_desc.requestFocus();

        // Setting current date and time.
        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        tv_date.setText(date_format.format(date));
        tv_time.setText(time_format.format(date));

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Start Date");

                final MaterialDatePicker start_date_picker = builder.build();

                start_date_picker.show(requireFragmentManager(), "DATE_PICKER");

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

                materialTimePicker.show(requireFragmentManager(), "TIME_PICKER");

                materialTimePicker.addOnPositiveButtonClickListener(dialog -> {

                    calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());

                    final Date date = calendar.getTime();
                    final String s_time = time_format.format(date);
                    tv_time.setText(s_time);

                });

            }
        });

        displayExpenseHeaders();

        btn_save_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeExpense();
            }
        });

        return view;

    }

    private void displayExpenseHeaders() {

        exp_head_adapter = new ExpenseTypeAdapter(getContext(), exp_heads_list, -1);
        exp_heads_recyclerView.setAdapter(exp_head_adapter);

        exp_head_adapter.setOnItemClickListener(new ExpenseTypeAdapter.onItemClickListener() {
            @Override
            public void onExpenseTypeSelect(String id, String expType) {

                final List<String> sub_header_list = new ArrayList<>();
                final List<List<String>> sub_head_list = new ArrayList<>();
                ListIterator<List<String>> iter = exp_sub_heads_list.listIterator();

                while (iter.hasNext()) {
                    List<String> dummyList = iter.next();
                    if (dummyList.get(1).equals(id)) {
                        sub_header_list.add(dummyList.get(2));
                        sub_head_list.add(dummyList);
                    }

                }

                smartMaterialSpinner.setItem(sub_header_list);

                smartMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "Selected Sub-Header : "+sub_head_list.get(position).toString());
                        final List<String> selectedSubHeader = sub_head_list.get(position);
                        exp_id = selectedSubHeader.get(0);
                        exp_p_id = selectedSubHeader.get(1);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            }
        });

    }

    private boolean isValidated() {

        boolean validated = true;

        final String desc, amount;

        desc = et_exp_desc.getText().toString();
        amount = et_amount.getText().toString();

        if (desc.length() == 0) {
            til_exp_desc.setError("Description can not be blank!");
            validated = false;
        }
        if (amount.length() == 0) {
            til_exp_amount.setError("Amount can not be ₹0!");
            validated = false;
        }
        if (exp_id.equals("") || exp_p_id.equals("")) {
            utilities.showTopSnackBar(getContext(), coordinatorLayout, "Please Select a Expense Header and Sub-Header!", R.color.internet_status_color);
            validated = false;
        }

        return validated;

    }

    private void clearFields() {

        til_exp_amount.setError(null);
        til_exp_desc.setError(null);
        et_exp_desc.setText("");
        et_amount.setText("");
        // Setting current date and time.
        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        tv_date.setText(date_format.format(date));
        tv_time.setText(time_format.format(date));

    }

    private void storeExpense() {

        if (isValidated()) {

            final String desc = et_exp_desc.getText().toString();
            final String amount = et_amount.getText().toString();
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
            expense.add(exp_p_id);
            expense.add(exp_id);
            expense.add(desc);
            expense.add(amount);
            expense.add(db_date);
            expense.add(time);

            dbHelper.storeExpense(expense, false);

            if (mListener != null) {
                clearFields();
                mListener.onExpenseAdded();
            }

        }

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
//        if (mListener != null) {
//            mListener.onClose();
//        }
    }

}