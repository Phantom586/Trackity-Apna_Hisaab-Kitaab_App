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

public class Expenses {

    private String ID, Head_ID, SubHead_ID, Description, Amount, Date, Time;

    public Expenses(String id, String h_id, String sh_id, String desc, String amt, String date, String time) {
        this.ID = id;
        this.Head_ID = h_id;
        this.SubHead_ID = sh_id;
        this.Amount = amt;
        this.Description = desc;
        this.Date = date;
        this.Time = time;
    }

    public String getID() { return ID; }

    public String getHead_ID() { return Head_ID; }

    public String getSubHead_ID() { return SubHead_ID; }

    public String getDescription() { return Description; }

    public String getAmount() { return Amount; }

    public String getDate() { return Date; }

    public String getTime() { return Time; }

}
