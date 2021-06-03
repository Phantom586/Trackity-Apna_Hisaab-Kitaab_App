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

public class ExpenseType {

    private String id, p_id, expenseType;
    private boolean isSelected;

    public ExpenseType(String id, String p_id,  String expType, boolean selected) {
        this.id = id;
        this.p_id = p_id;
        this.expenseType = expType;
        this.isSelected = selected;
    }

    public String getId() { return id; }

    public String getP_id() { return p_id; }

    public String getExpenseType() { return expenseType; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

}

