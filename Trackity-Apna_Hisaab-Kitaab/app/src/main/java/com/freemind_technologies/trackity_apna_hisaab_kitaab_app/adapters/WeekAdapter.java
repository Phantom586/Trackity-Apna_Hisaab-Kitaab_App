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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.R;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.Week;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WAViewHolder> {

    private WeekAdapter.onItemClickListener mListener;
    private final String TAG = "WeekAdapter";
    private static int lastSelectedPos = -1;
    private List<Week> weekList;
    private Context context;

    public interface  onItemClickListener{
        void onWeekClicked(Date date);
    }

    public void setOnItemClickListener(WeekAdapter.onItemClickListener listener){
        mListener = listener;
    }

    public WeekAdapter(Context ctx, List<Week> wList) {
        this.context = ctx;
        weekList = wList;
    }

    @NonNull
    @Override
    public WAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.week_list_item, parent, false);
        return new WeekAdapter.WAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WAViewHolder holder, int position) {

        final Week week = weekList.get(holder.getAdapterPosition());

        holder.tv_day.setText(week.getWeekDay());

        holder.tv_date.setText(week.getWeekDate());

        if (week.isSelected()){
            Log.d(TAG, "Week Today : "+week.getWeekDate());

            lastSelectedPos = holder.getAdapterPosition();
            holder.tv_day.setTextColor(0xFFFFFFFF);
            holder.tv_date.setTextColor(0xFFFFFFFF);

            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.all_round_corners_blue));

        } else {
            holder.tv_day.setTextColor(context.getResources().getColor(R.color.text_color_1));
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.text_color_2));
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.activity_bg));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {

                    final int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (!week.isSelected()) {
                            week.setSelected(!week.isSelected());
                            holder.tv_day.setTextColor(0xFFFFFFFF);
                            holder.tv_date.setTextColor(0xFFFFFFFF);
                            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.all_round_corners_blue));
                        }

                        if (lastSelectedPos != -1 && lastSelectedPos != position) {

                            Week lastWeek = weekList.get(lastSelectedPos);
                            lastWeek.setSelected(!lastWeek.isSelected());
                            notifyItemChanged(lastSelectedPos);

                        }

                        lastSelectedPos = position;

                        mListener.onWeekClicked(week.getDateObj());

                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() { return weekList.size(); }

    class WAViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private TextView tv_day, tv_date;

        public WAViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.wli_linear_layout);
            tv_date = itemView.findViewById(R.id.wli_date);
            tv_day = itemView.findViewById(R.id.wli_day);

        }
    }

}