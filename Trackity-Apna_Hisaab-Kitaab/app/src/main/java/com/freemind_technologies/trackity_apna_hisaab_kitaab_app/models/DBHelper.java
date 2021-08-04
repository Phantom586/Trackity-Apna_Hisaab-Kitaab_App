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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private MySharedPreferences mySharedPrefs;

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "Trackity_AHK.db";
    private static final String TABLE_EXPENSES = "Expenses_Table";
    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Head_ID TEXT, SubHead_ID TEXT," +
            " Description TEXT, Amount TEXT, Date TEXT, Time TEXT, Updated TEXT, Stored TEXT)";
    /* Expenses Table's Columns */
    private static final String exp_col_0 = "ID";
    private static final String exp_col_1 = "Head_ID";
    private static final String exp_col_2 = "SubHead_ID";
    private static final String exp_col_3 = "Description";
    private static final String exp_col_4 = "Amount";
    private static final String exp_col_5 = "Date";
    private static final String exp_col_6 = "Time";
    private static final String exp_col_7 = "Updated";
    private static final String exp_col_8 = "Stored";

    private static final String TABLE_EXPENSE_TYPES = "Expense_Types";
    private static final String CREATE_TABLE_EXPENSE_TYPES = "CREATE TABLE " + TABLE_EXPENSE_TYPES + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Parent_ID INTEGER, Name TEXT, " +
            " Stored TEXT)";
    /* Expense_Types Table's Columns */
    private static final String exp_type_col_0 = "ID";
    private static final String exp_type_col_1 = "Parent_ID";
    private static final String exp_type_col_2 = "Name";
    private static final String exp_type_col_3 = "Stored";

    private static final String TABLE_DELETED = "Deleted";
    private static final String CREATE_TABLE_DELETED = "CREATE TABLE " + TABLE_DELETED + " (ID INTEGER NOT NULL, Table_Name TEXT, PRIMARY KEY (ID, Table_Name))";
    /* Deleted Table's Columns */
    private static final String del_col_0 = "ID";
    private static final String del_col_1 = "Table_Name";

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        mySharedPrefs = new MySharedPreferences(context);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_EXPENSE_TYPES);
        db.execSQL(CREATE_TABLE_DELETED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_EXPENSE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DELETED);
        onCreate(db);

    }

    public boolean storeExpense(List<String> expense, boolean containsID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (containsID) {

            contentValues.put(exp_col_0, expense.get(0));
            contentValues.put(exp_col_1, expense.get(1));
            contentValues.put(exp_col_2, expense.get(2));
            contentValues.put(exp_col_3, expense.get(3));
            contentValues.put(exp_col_4, expense.get(4));
            contentValues.put(exp_col_5, expense.get(5));
            contentValues.put(exp_col_6, expense.get(6));
            contentValues.put(exp_col_7, "No");
            contentValues.put(exp_col_8, "Yes");

        } else {

            contentValues.put(exp_col_1, expense.get(0));
            contentValues.put(exp_col_2, expense.get(1));
            contentValues.put(exp_col_3, expense.get(2));
            contentValues.put(exp_col_4, expense.get(3));
            contentValues.put(exp_col_5, expense.get(4));
            contentValues.put(exp_col_6, expense.get(5));
            contentValues.put(exp_col_7, "No");
            contentValues.put(exp_col_8, "No");

        }

        Log.d(TAG, "Adding Expense : "+contentValues.toString());

        long res = db.insert(TABLE_EXPENSES, null, contentValues);
        db.close();
        if(res == -1){
            return false;
        } else {
            return true;
        }

    }

    public void deleteAllRows__ExpenseTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, null, null);

        db.close();

    }

    public void deleteAllRows__ExpenseTypesTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE_TYPES, null, null);

        db.close();

    }

    public boolean storeExpenseTypes(String ID, String P_ID, String name, boolean fromServer) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (!ID.equals(""))
            contentValues.put(exp_type_col_0, ID);
        contentValues.put(exp_type_col_1, P_ID);
        contentValues.put(exp_type_col_2, name);
        if (fromServer)
            contentValues.put(exp_type_col_3, "Yes");
        else
            contentValues.put(exp_type_col_3, "No");

        long res = db.insert(TABLE_EXPENSE_TYPES, null, contentValues);
        db.close();
        if(res == -1){
            return false;
        } else {
            return true;
        }

    }

    public String storeNewExpHeads(String name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(exp_type_col_1, "0");
        contentValues.put(exp_type_col_2, name);
        contentValues.put(exp_type_col_3, "No");

        long res = db.insert(TABLE_EXPENSE_TYPES, null, contentValues);
        db.close();
        Log.d(TAG, "Newly Inserted ExpHead Result ->"+res);
        return String.valueOf(res);

    }

    public void storeNewExpSubHeads(String head_id, String name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(exp_type_col_1, head_id);
        contentValues.put(exp_type_col_2, name);
        contentValues.put(exp_type_col_3, "No");

        long res = db.insert(TABLE_EXPENSE_TYPES, null, contentValues);
        db.close();

    }

    public Cursor fetchExpenses(String id, String expType, String sDate, String eDate) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        if (expType.equals("All")) {
            res = db.query(TABLE_EXPENSES, null, "Date BETWEEN ? AND ?", new String[] {sDate, eDate}, null, null, "Date DESC");
        } else {
            res = db.query(TABLE_EXPENSES, null, "HEAD_ID=? AND Date BETWEEN ? AND ?", new String[] {id, sDate, eDate}, null, null, "Date DESC");
        }

        return res;

    }

    public Cursor fetchExpenseTypes(boolean isHead) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        if (isHead)
            res = db.query(TABLE_EXPENSE_TYPES, null, "Parent_ID=?", new String[] {"0"}, null, null, null);
        else
            res = db.query(TABLE_EXPENSE_TYPES, null, "Parent_ID IS NOT ?", new String[] {"0"}, null, null, null);

        return res;

    }

    public Cursor fetchAllExpenseTypes() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(TABLE_EXPENSE_TYPES, null, null, null, null, null, null);

        return res;

    }

    public Cursor fetchSubHeadByID(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        res = db.query(TABLE_EXPENSE_TYPES, new String[] {"Name"}, "ID=?", new String[] {id}, null, null, null);

        return res;

    }

    public Cursor fetchExpTypes__byID(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        res = db.query(TABLE_EXPENSE_TYPES, null, "Parent_ID=?", new String[] {id}, null, null, null);

        return res;

    }

    public String fetchExpTotalAmt__ByH_IDAndSH_ID(String h_id, String sh_id, String s_date, String e_date) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.query(TABLE_EXPENSES, new String[] {"SUM(Amount)"}, "Head_ID=? AND SubHead_ID=? AND Date BETWEEN ? AND ?", new String[] {h_id, sh_id, s_date, e_date}, null, null, null);
        res.moveToNext();

        String total_expense = res.getString(0);
        if (total_expense == null)
            total_expense = "0";
        db.close();

        return total_expense;

    }

    public Cursor fetchExpenses__bySubHeadID(String h_id, String sh_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        res = db.query(TABLE_EXPENSES, null, "Head_ID=? AND SubHead_ID=?", new String[] {h_id, sh_id}, null, null, null);

        return res;

    }

    public void deleteExpense__byID(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, exp_col_0 + "=?", new String[] {id});
        db.close();

    }

    public void deleteExpenseType__byID(String id, boolean isHead) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE_TYPES, exp_type_col_0 + "=?", new String[] {id});

        if (isHead)
            db.delete(TABLE_EXPENSE_TYPES, exp_type_col_1 + "=?", new String[] {id});
        db.close();

    }

    public Cursor fetchExpenseById(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;

        res = db.query(TABLE_EXPENSES, null, "ID=?", new String[] {id}, null, null, null);

        return res;

    }

    public String updateExpense(String id, List<String> expData) {

        SQLiteDatabase db = this.getWritableDatabase();
        int res;
        String syncType = "update";

        Cursor checkStoredInDB = db.query(TABLE_EXPENSES, null, "Stored=? AND ID=?", new String[] {"No", id}, null, null, null);
        checkStoredInDB.moveToFirst();
        boolean notStoredInDB = checkStoredInDB.getCount() > 0;

        final ContentValues cv = new ContentValues();
        cv.put(exp_col_1, expData.get(0));
        cv.put(exp_col_2, expData.get(1));
        cv.put(exp_col_3, expData.get(2));
        cv.put(exp_col_4, expData.get(3));
        cv.put(exp_col_5, expData.get(4));
        cv.put(exp_col_6, expData.get(5));
        if (notStoredInDB){
            cv.put(exp_col_7, "No");
            syncType = "store";
        }
        else
            cv.put(exp_col_7, "Yes");

        Log.d(TAG, "Updating Expense : "+cv.toString());

        res = db.update(TABLE_EXPENSES, cv, "ID=?", new String[] {id});
        db.close();
        return syncType;

    }

    public Cursor fetchExpenseDates(String sDate, String eDate) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res;
        res = db.query(TABLE_EXPENSES, new String[] {"Date"}, "Date BETWEEN ? AND ?", new String[] {sDate, eDate}, null, null, null);

        return res;

    }

    public Cursor fetchExpensesTotAmt__BetweenDates(String sDate, String eDate) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res;
        res = db.query(TABLE_EXPENSES, new String[] {"SUM(Amount)"}, "Date BETWEEN ? AND ?", new String[] {sDate, eDate}, null, null, null);

        return res;

    }

    public Cursor fetchExpensesTotAmt__BetweenDates__byHeadID(String sDate, String eDate, String headID) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res;
        res = db.query(TABLE_EXPENSES, new String[] {"SUM(Amount)"}, "Head_ID=? AND Date BETWEEN ? AND ?", new String[] {headID, sDate, eDate}, null, null, null);

        return res;

    }

    public Cursor fetchTodayTotalExpense(String expHeadID, String expType, String sDate, String eDate) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (expType.equals("All")) {
            res = db.query(TABLE_EXPENSES, new String[] {"SUM(Amount)"}, "Date BETWEEN ? AND ?", new String[] {sDate, eDate}, null, null, null);
        } else {
            res = db.query(TABLE_EXPENSES, new String[] {"SUM(Amount)"}, "Head_ID=? AND Date BETWEEN ? AND ?", new String[] {expHeadID, sDate, eDate}, null, null, null);
        }

        return res;

    }

    public boolean ExpenseTableHasData() {

        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor res = db.query(TABLE_EXPENSES, null, null, null, null, null, null);
        count = res.getCount();
        db.close();
        return count > 0;

    }

    public String composeJSONFromSQLite__ExpenseTable(boolean store, boolean update, boolean delete, boolean fullDump) throws JSONException {

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject dbDump = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray0 = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();

        final String u_id = mySharedPrefs.getUserID();
        dbDump.put("User_ID", u_id);
        Log.d(TAG, "User_ID -> "+u_id);

        if (delete || fullDump) {

            final Cursor res = db.query(TABLE_DELETED, null, "Table_Name=?", new String[] {"Expenses"}, null, null, null);

            if (res.moveToFirst()) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(del_col_0, res.getString(0));

                    jsonArray0.put(jsonObject);

                } while (res.moveToNext());
            }
            res.close();

        }
        dbDump.put("delete", jsonArray0);

        if (store || fullDump) {

            final Cursor res = db.query(TABLE_EXPENSES, null, "Stored=?", new String[] {"No"}, null, null, null);

            if (res.moveToFirst()) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(exp_col_0, res.getString(0));
                    jsonObject.put(exp_col_1, res.getString(1));
                    jsonObject.put(exp_col_2, res.getString(2));

                    String desc = res.getString(3);
                    // Checking if the Description contains an apostrophe character.
                    if (desc.contains("'")) {
                        desc = desc.replace("'", "''");
                    }

                    jsonObject.put(exp_col_3, desc);
                    jsonObject.put(exp_col_4, res.getString(4));
                    jsonObject.put(exp_col_5, res.getString(5));
                    jsonObject.put(exp_col_6, res.getString(6));

                    jsonArray.put(jsonObject);

                } while (res.moveToNext());
            }
            res.close();

        }
        dbDump.put("store", jsonArray);

        if (update || fullDump) {

            final Cursor res = db.query(TABLE_EXPENSES, null, "Updated=?", new String[] {"Yes"}, null, null, null);

            if (res.moveToFirst()) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(exp_col_0, res.getString(0));
                    jsonObject.put(exp_col_1, res.getString(1));
                    jsonObject.put(exp_col_2, res.getString(2));

                    String desc = res.getString(3);
                    // Checking if the Description contains an apostrophe character.
                    if (desc.contains("'")) {
                        desc = desc.replace("'", "''");
                    }

                    jsonObject.put(exp_col_3, desc);
                    jsonObject.put(exp_col_4, res.getString(4));
                    jsonObject.put(exp_col_5, res.getString(5));
                    jsonObject.put(exp_col_6, res.getString(6));

                    jsonArray1.put(jsonObject);

                } while (res.moveToNext());
            }
            res.close();

        }
        dbDump.put("update", jsonArray1);

        db.close();

        return dbDump.toString();

    }

    public String composeJSONFromSQLite__ExpenseTypes(boolean store, boolean delete, boolean fullDump) throws JSONException {

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject dbDump = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();

        final String u_id = mySharedPrefs.getUserID();
        dbDump.put("User_ID", u_id);
        Log.d(TAG, "User_ID -> "+u_id);

        if (delete || fullDump) {

            final Cursor res = db.query(TABLE_DELETED, null, "Table_Name=?", new String[] {"Expense_Types"}, null, null, null);

            if (res.moveToFirst()) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(del_col_0, res.getString(0));

                    jsonArray1.put(jsonObject);

                } while (res.moveToNext());
            }
            res.close();

        }
        dbDump.put("delete", jsonArray1);

        if (store || fullDump) {

            final Cursor res = db.query(TABLE_EXPENSE_TYPES, null, "Stored=?", new String[] {"No"}, null, null, null);

            if (res.moveToFirst()) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(exp_type_col_0, res.getString(0));
                    jsonObject.put(exp_type_col_1, res.getString(1));
                    jsonObject.put(exp_type_col_2, res.getString(2));

                    jsonArray.put(jsonObject);

                } while (res.moveToNext());
            }

            res.close();

        }
        dbDump.put("store", jsonArray);

        db.close();

        return dbDump.toString();

    }

    public void updateSyncStatus__ExpenseTable(String id, String syncType, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        cv.put(syncType, status);

        int res = db.update(TABLE_EXPENSES, cv, "ID=?", new String[] {id});
        db.close();

    }

    public int dbSyncCount__ExpenseTypes() {

        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.query(TABLE_EXPENSE_TYPES, null, "Stored=?", new String[] {"No"}, null, null, null);
        count = res.getCount();

        res.close();
        db.close();

        return count;

    }

    public void updateSyncStatus__ExpenseTypes(String id, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        cv.put("Stored", status);

        int res = db.update(TABLE_EXPENSE_TYPES, cv, "ID=?", new String[] {id});
        db.close();

    }

    public void updateExpenseTypes__byID(String id, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("Stored", "No");

        int res = db.update(TABLE_EXPENSE_TYPES, cv, "ID=?", new String[] {id});
        db.close();

    }


    public boolean storeDeletedItem(String ID, String Table_Name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(del_col_0, ID);
        contentValues.put(del_col_1, Table_Name);

        Log.d(TAG, "Deleted Item : "+contentValues.toString());

        long res = db.insert(TABLE_DELETED, null, contentValues);
        db.close();
        if(res == -1){
            return false;
        } else {
            return true;
        }

    }

}
