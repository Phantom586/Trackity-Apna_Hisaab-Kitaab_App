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

public class SubHeadSummary {

    private String expenseName, total_amount, subHeadID, headID;

    public SubHeadSummary(String expName, String tAmt, String hID, String shID) {
        this.expenseName = expName;
        this.total_amount = tAmt;
        this.headID = hID;
        this.subHeadID = shID;
    }

    public String getExpSubHeadName() { return expenseName; }

    public String getTotal_amount() { return total_amount; }

    public String getSubHeadID() { return subHeadID; }

    public String getHeadID() { return headID; }

}
