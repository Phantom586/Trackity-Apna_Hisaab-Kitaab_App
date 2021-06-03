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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Expenses;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.ExpenseDetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseCalendarAdapter extends RecyclerView.Adapter<ExpenseCalendarAdapter.ECAViewHolder> {

    private List<String> expenseTypePopUpList;
    private List<Expenses> expensesList;
    private Context context;

    public ExpenseCalendarAdapter(Context ctx, List<Expenses> exList, List<String> expPopList) {
        this.context = ctx;
        expensesList = exList;
        expenseTypePopUpList = expPopList;
    }

    @NonNull
    @Override
    public ECAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expense_calendar_item, parent, false);
        return new ExpenseCalendarAdapter.ECAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ECAViewHolder holder, int position) {

        final Expenses expenses = expensesList.get(holder.getAdapterPosition());

        holder.tv_exp_desc.setText(expenses.getDescription());

        final String amt = "₹" + expenses.getAmount();
        holder.tv_exp_amount.setText(amt);

        holder.tv_exp_time.setText(expenses.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(v.getContext(), ExpenseDetails.class);
                in.putExtra("expID", String.valueOf(expenses.getID()));
                in.putExtra("exp_Head_ID", String.valueOf(expenses.getHead_ID()));
                in.putExtra("exp_Subhead_ID", String.valueOf(expenses.getSubHead_ID()));
                in.putExtra("comingFrom", "ECA");
                v.getContext().startActivity(in);

            }
        });

    }

    @Override
    public int getItemCount() { return expensesList.size(); }

    public class ECAViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_exp_desc, tv_exp_amount, tv_exp_time;

        public ECAViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_exp_amount = itemView.findViewById(R.id.eci_exp_amt);
            tv_exp_time = itemView.findViewById(R.id.eci_exp_time);
            tv_exp_desc = itemView.findViewById(R.id.eci_exp_desc);

        }
    }

}