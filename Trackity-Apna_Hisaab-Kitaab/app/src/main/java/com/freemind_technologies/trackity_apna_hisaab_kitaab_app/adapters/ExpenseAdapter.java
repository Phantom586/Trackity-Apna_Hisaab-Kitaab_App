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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Expenses;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.DBHelper;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.ExpenseDetails;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.EAViewHolder> {

    private SimpleDateFormat local_db_date_format, db_date_format;
    private ExpenseAdapter.onItemClickListener mListener;
    private final String TAG = "ExpenseAdapter";
    private List<Expenses> expensesList;
    private boolean isSubHeadExpense;
    private String comingFrom;
    private DBHelper dbHelper;
    private Context context;

    public interface  onItemClickListener{
        void onDeleteExpense(String expId, int position);
    }

    public void setOnItemClickListener(ExpenseAdapter.onItemClickListener listener){
        mListener = listener;
    }

    public ExpenseAdapter(Context ctx, List<Expenses> exList, boolean shExpense, String cFrom) {
        this.context = ctx;
        expensesList = exList;
        dbHelper = new DBHelper(this.context);
        isSubHeadExpense = shExpense;
        comingFrom = cFrom;
        db_date_format = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        local_db_date_format = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
    }

    @NonNull
    @Override
    public EAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expense_item_v2, parent, false);
        return new ExpenseAdapter.EAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EAViewHolder holder, int position) {

        final Expenses expenses = expensesList.get(holder.getAdapterPosition());

        holder.tv_desc.setText(expenses.getDescription());

        final String amt = "₹" + expenses.getAmount();
        holder.tv_amt.setText(amt);

        Cursor res = dbHelper.fetchSubHeadByID(expenses.getSubHead_ID());
        res.moveToNext();

        if (!isSubHeadExpense)
            holder.tv_subHead.setText(res.getString(0));
        else {
            holder.tv_subHead.setVisibility(View.GONE);
            try {

                holder.tv_date.setVisibility(View.VISIBLE);
                final Date date_obj = db_date_format.parse(expenses.getDate());
                final String date = local_db_date_format.format(date_obj);
                holder.tv_date.setText(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();

        holder.tv_time.setText(expenses.getTime());

        holder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(v.getContext(), ExpenseDetails.class);
                in.putExtra("expID", String.valueOf(expenses.getID()));
                in.putExtra("exp_Head_ID", String.valueOf(expenses.getHead_ID()));
                in.putExtra("exp_Subhead_ID", String.valueOf(expenses.getSubHead_ID()));
                in.putExtra("comingFrom", comingFrom);
                v.getContext().startActivity(in);

            }
        });

        holder.ll_delete_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle(v.getContext().getResources().getString(R.string.ea_del_header))
                        .setMessage(v.getContext().getResources().getString(R.string.ea_del_desc))
                        .setPositiveButton(v.getContext().getResources().getString(R.string.ea_positive_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onDeleteExpense(String.valueOf(expenses.getID()), holder.getAdapterPosition());
                                }
                            }
                        })
                        .setNegativeButton(v.getContext().getResources().getString(R.string.ea_negative_btn_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() { return expensesList.size(); }

    class EAViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_desc, tv_amt, tv_time, tv_subHead, tv_date;
        private LinearLayout ll_delete_expense, ll_content;

        public EAViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_delete_expense = itemView.findViewById(R.id.ei_delete_expense);
            tv_subHead = itemView.findViewById(R.id.ei_exp_sub_head);
            ll_content = itemView.findViewById(R.id.ei_content);
            tv_desc = itemView.findViewById(R.id.ei_exp_desc);
            tv_date = itemView.findViewById(R.id.ei_exp_date);
            tv_time = itemView.findViewById(R.id.ei_exp_time);
            tv_amt = itemView.findViewById(R.id.ei_exp_amt);

        }
    }

}
