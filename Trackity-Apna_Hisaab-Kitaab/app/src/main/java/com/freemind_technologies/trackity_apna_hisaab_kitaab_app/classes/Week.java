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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes;

import java.util.Date;

public class Week {

    private String weekDate, weekDay;
    private boolean isSelected;
    private Date dateObj;

    public Week(String wDay, String wDate, boolean select, Date dObj) {
        this.weekDate = wDate;
        this.weekDay = wDay;
        this.dateObj = dObj;
        this.isSelected = select;
    }

    public String getWeekDate() { return weekDate; }

    public String getWeekDay() { return weekDay; }

    public Date getDateObj() { return dateObj; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

}

