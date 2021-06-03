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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseTypeAdapter extends RecyclerView.Adapter<ExpenseTypeAdapter.ETAViewHolder> {

    private String TAG = "ExpenseTypeAdapter";
    private List<ExpenseType> expenseTypeList;
    private onItemClickListener mListener;
    private int lastSelectedPos;
    private Context context;

    public interface  onItemClickListener{
        void onExpenseTypeSelect(String id, String expType);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public ExpenseTypeAdapter(Context ctx, List<ExpenseType> expList, int lastPos) {
        this.context = ctx;
        expenseTypeList = expList;
        lastSelectedPos = lastPos;
    }

    @NonNull
    @Override
    public ETAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expense_type_card, parent, false);
        return new ExpenseTypeAdapter.ETAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ETAViewHolder holder, int position) {

        final ExpenseType expenseType = expenseTypeList.get(holder.getAdapterPosition());

        final boolean isSelected = expenseType.isSelected();
        if (isSelected) {
            holder.ll_exp.setBackgroundColor(this.context.getResources().getColor(R.color.purple_200));
        } else {
            holder.ll_exp.setBackground(this.context.getResources().getDrawable(R.drawable.bg_gradient_secondary));
        }

        holder.tv_exp_name.setText(expenseType.getExpenseType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    final int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        /* Making the current ExpenseType Selected */
                        if (!expenseType.isSelected()) {
                            expenseType.setSelected(!expenseType.isSelected());
                            holder.ll_exp.setBackgroundColor(context.getResources().getColor(R.color.purple_200));
                        }

                        if (lastSelectedPos != position && lastSelectedPos != -1) {

                            ExpenseType lastSelected = expenseTypeList.get(lastSelectedPos);
                            lastSelected.setSelected(!lastSelected.isSelected());
                            notifyItemChanged(lastSelectedPos);

                        }

                        lastSelectedPos = position;

                        final String expType = expenseType.getExpenseType();
                        mListener.onExpenseTypeSelect(expenseType.getId(), expType);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() { return expenseTypeList.size(); }

    public class ETAViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_exp_name;
        private LinearLayout ll_exp;

        public ETAViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_exp_name = itemView.findViewById(R.id.etc_exp_name);
            ll_exp = itemView.findViewById(R.id.etc_exp_linearLayout);

        }

    }

}
