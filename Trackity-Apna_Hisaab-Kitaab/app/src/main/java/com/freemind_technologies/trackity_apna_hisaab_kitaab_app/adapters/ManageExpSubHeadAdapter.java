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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ExpenseType;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManageExpSubHeadAdapter extends RecyclerView.Adapter<ManageExpSubHeadAdapter.ViewHolder> {

    private ManageExpSubHeadAdapter.onItemClickListener mListener;
    private String TAG = "ExpenseSubHeadAdapter";
    private List<ExpenseType> expenseTypeList;
    private Context context;

    public interface  onItemClickListener{
        void onExpenseSubHeadSelect(String id, String p_id, String expType);
        void onExpenseDelete(int position, String id);
    }

    public void setOnItemClickListener(ManageExpSubHeadAdapter.onItemClickListener listener){
        mListener = listener;
    }

    public ManageExpSubHeadAdapter(Context ctx, List<ExpenseType> expList) {
        this.context = ctx;
        expenseTypeList = expList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mg_exp_head_item, parent, false);
        return new ManageExpSubHeadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        final ExpenseType expenseType = expenseTypeList.get(holder.getAdapterPosition());

        holder.tv_exp_name.setText(expenseType.getExpenseType());

//        holder.ll_exp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Expense Item Clicked : "+expenseType.getExpenseType());
//                if (mListener != null) {
//                    final int position = holder.getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        /* Making the current ExpenseType Selected */
//                        if (!expenseType.isSelected()) {
//                            expenseType.setSelected(!expenseType.isSelected());
//                            holder.ll_exp.setBackgroundColor(context.getResources().getColor(R.color.purple_200));
//                        }
//
//                        if (lastSelectedPos != -1 && lastSelectedPos != position) {
//
//                            ExpenseType lastSelected = expenseTypeList.get(lastSelectedPos);
//                            lastSelected.setSelected(!lastSelected.isSelected());
//                            notifyItemChanged(lastSelectedPos);
//
//                        }
//
//                        lastSelectedPos = position;
//
//                        final String expType = expenseType.getExpenseType();
//                        mListener.onExpenseSubHeadSelect(expenseType.getId(), expenseType.getP_id(), expType);
//                    }
//                }
//            }
//        });

        holder.im_exp_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Delete Clicked for Exp : "+expenseType.getExpenseType());

                final int position = holder.getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    if (mListener != null) {
                        mListener.onExpenseDelete(position, expenseType.getId());
                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() { return expenseTypeList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TableLayout ll_exp;
        private TextView tv_exp_name;
        private ImageView im_exp_delete;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ll_exp = itemView.findViewById(R.id.mehi_tableLayout);
            tv_exp_name = itemView.findViewById(R.id.mehi_exp_name);
            im_exp_delete = itemView.findViewById(R.id.mehi_delete_exp);

        }
    }

}

