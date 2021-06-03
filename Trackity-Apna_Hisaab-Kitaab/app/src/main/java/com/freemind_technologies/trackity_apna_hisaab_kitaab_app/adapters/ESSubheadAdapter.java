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

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.SubHeadSummary;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models.Utilities;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.views.SubHeaderExpenses;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ESSubheadAdapter extends RecyclerView.Adapter<ESSubheadAdapter.ViewHolder> {

    private CoordinatorLayout coordinatorLayout;
    private List<SubHeadSummary> subHeadList;
    private Utilities utilities;
    private String dateRange;
    private Context context;

    public ESSubheadAdapter(Context ctx, List<SubHeadSummary> shList, CoordinatorLayout cLayout, String dRange) {
        this.context = ctx;
        subHeadList = shList;
        coordinatorLayout = cLayout;
        utilities = new Utilities(ctx);
        dateRange = dRange;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.exp_summary_item, parent, false);
        return new ESSubheadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        final SubHeadSummary subHeadSummary = subHeadList.get(holder.getAdapterPosition());

        holder.tv_subHead_Name.setText(subHeadSummary.getExpSubHeadName());

        final String total_amt = subHeadSummary.getTotal_amount();
        final String total_amt_disp = "₹" + subHeadSummary.getTotal_amount();
        holder.tv_total_amt.setText(total_amt_disp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!total_amt.equals("0")) {
                    Intent in = new Intent(v.getContext(), SubHeaderExpenses.class);
                    in.putExtra("subHeadID", subHeadSummary.getSubHeadID());
                    in.putExtra("headID", subHeadSummary.getHeadID());
                    in.putExtra("subHeadName", subHeadSummary.getExpSubHeadName());
                    in.putExtra("totalAmount", subHeadSummary.getTotal_amount());
                    in.putExtra("dateRange", dateRange);
                    v.getContext().startActivity(in);
                } else {
                    utilities.showTopSnackBar(v.getContext(),coordinatorLayout, "There are No Expenses in this Sub-Head!", R.color.internet_status_color);
                }

            }
        });

    }

    @Override
    public int getItemCount() { return subHeadList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_subHead_Name, tv_total_amt;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tv_subHead_Name = itemView.findViewById(R.id.esi_subHead_Name);
            tv_total_amt = itemView.findViewById(R.id.esi_total_amt);

        }
    }

}
