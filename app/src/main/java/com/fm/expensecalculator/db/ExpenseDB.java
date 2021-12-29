package com.fm.expensecalculator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.fm.expensecalculator.R;
import com.fm.expensecalculator.db.models.ExpenseModel;
import com.fm.expensecalculator.db.models.SheetModel;

import java.util.ArrayList;

public class ExpenseDB extends SQLiteOpenHelper {

    private static String DB_NAME = "expense_calc.db";
    private static int DB_VERSION = 1;

    //for expense table
    private static final String TABLE_EXPENSE = "expense";
    private static final String FIELD_ID = "id_expense";
    private static final String FIELD__REMARKS = "remarks";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_IS_REGULAR = "is_regular";

    //foreign key references sheet table's month id
    private static final String FIELD_MONTH_ID = "id_month";

    //for sheets table
    private static final String TABLE_SHEETS = "sheets";
    private static final String FIELD_MONTH = "month";
    private static final String FIELD_YEAR = "year";
    private static final String FIELD_INCOME = "income";

    private Context context;
    private SQLiteDatabase database;

    private static final String CREATE_TABLE_EXPENSE = "create table if not exists " + TABLE_EXPENSE
            + " ("
            + FIELD_ID + " integer primary key autoincrement,"
            + FIELD__REMARKS + " text,"
            + FIELD_AMOUNT + " double not null,"
            + FIELD_IS_REGULAR + " text not null,"
            + FIELD_DATE + " int not null,"
            + FIELD_MONTH_ID + " integer not null,"
            + " FOREIGN KEY (" + FIELD_MONTH_ID + ") REFERENCES " + TABLE_SHEETS + "(" + FIELD_MONTH_ID + ")"
            + ")";

    private static final String CREATE_TABLE_SHEETS = "create table if not exists " + TABLE_SHEETS
            + " ("
            + FIELD_MONTH_ID + " integer primary key autoincrement,"
            + FIELD_MONTH + " int not null,"
            + FIELD_YEAR + " int not null,"
            + FIELD_INCOME + " double not null"
            + ")";

    public ExpenseDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SHEETS);
        db.execSQL(CREATE_TABLE_EXPENSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHEETS);
        onCreate(db);
    }

    //add a new sheet in sheets table
    public boolean addNewSheet(String month, String year, double income) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_MONTH, month);
        contentValues.put(FIELD_YEAR, year);
        contentValues.put(FIELD_INCOME, income);
        if (checkIfSheetExists(month, year)) {
            database.insert(TABLE_SHEETS, null, contentValues);
            database.close();
            return true;
        } else {
            return false;
        }
    }

    //returns the list of all expenses from the expense table
    public ArrayList<ExpenseModel> getExpenses(String month_id) {
        ArrayList<ExpenseModel> expenseModelList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_EXPENSE, null, FIELD_MONTH_ID + "=?", new String[]{month_id}, null, null, FIELD_DATE);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            ExpenseModel expenseMode = new ExpenseModel();
            expenseMode.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            expenseMode.setRemarks(cursor.getString(cursor.getColumnIndex(FIELD__REMARKS)));
            expenseMode.setAmount(cursor.getString(cursor.getColumnIndex(FIELD_AMOUNT)));
            expenseMode.setDate(cursor.getString(cursor.getColumnIndex(FIELD_DATE)));
            expenseMode.setRegular(getBooleanFormat(cursor.getInt(cursor.getColumnIndex(FIELD_IS_REGULAR))));
            expenseModelList.add(expenseMode);
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return expenseModelList;
    }

    //returns the sheet data for a specific month
    public SheetModel getIncomeData(String month_id) {
        SheetModel sheetModel = new SheetModel();
        Cursor cursor = database.query(TABLE_SHEETS, null, FIELD_MONTH_ID + "=?", new String[]{month_id}, null, null, null);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            sheetModel.setId(cursor.getInt(cursor.getColumnIndex(FIELD_MONTH_ID)));
            sheetModel.setIncome(cursor.getDouble(cursor.getColumnIndex(FIELD_INCOME)));
            sheetModel.setMonth(getMonthName(cursor.getString(cursor.getColumnIndex(FIELD_MONTH))));
            sheetModel.setYear(cursor.getString(cursor.getColumnIndex(FIELD_YEAR)));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return sheetModel;
    }

    //edit the income in sheets table
    public void editIncome(double income, String month_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_INCOME, income);
        String[] args = {month_id};
        database.update(TABLE_SHEETS, contentValues, FIELD_MONTH_ID + "=?", args);
        database.close();
    }

    //edit expense entry with the specific id
    public void updateExpense(String amount, String remarks, String date, boolean bool, int sel_expense_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD__REMARKS, remarks);
        contentValues.put(FIELD_AMOUNT, amount);
        contentValues.put(FIELD_DATE, date);
        contentValues.put(FIELD_IS_REGULAR, bool);
        String[] args = {String.valueOf(sel_expense_id)};
        database.update(TABLE_EXPENSE, contentValues, FIELD_ID + "=?", args);
        database.close();
    }

    //returns the list of all sheets from the sheet table
    public ArrayList<SheetModel> getSheets() {
        ArrayList<SheetModel> sheetModelList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_SHEETS, null, null, null, null, null, FIELD_YEAR + " , " + FIELD_MONTH);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            SheetModel sheetModel = new SheetModel();
            sheetModel.setId(cursor.getInt(cursor.getColumnIndex(FIELD_MONTH_ID)));
            sheetModel.setIncome(cursor.getDouble(cursor.getColumnIndex(FIELD_INCOME)));
            sheetModel.setMonth(getMonthName(cursor.getString(cursor.getColumnIndex(FIELD_MONTH))));
            sheetModel.setYear(cursor.getString(cursor.getColumnIndex(FIELD_YEAR)));
            sheetModelList.add(sheetModel);
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return sheetModelList;
    }

    //add a new expense entry in expense table
    public void addExpense(String amount, String remarks, String date, boolean bool, String sel_month_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD__REMARKS, remarks);
        contentValues.put(FIELD_AMOUNT, amount);
        contentValues.put(FIELD_DATE, date);
        contentValues.put(FIELD_IS_REGULAR, bool);
        contentValues.put(FIELD_MONTH_ID, sel_month_id);
        database.insert(TABLE_EXPENSE, null, contentValues);
        database.close();
    }

    //delete expense by id
    public void deleteExpense(long expense_id) {
        database.delete(TABLE_EXPENSE, FIELD_ID + "=?", new String[]{String.valueOf(expense_id)});
        database.close();
    }

    //returns total expense for specific month
    public double getTotalExpenseMonthly(int month_id) {
        double total_expense = 0;
        Cursor cursor = database.query(TABLE_EXPENSE, new String[]{FIELD_AMOUNT}, FIELD_MONTH_ID + "=?", new String[]{String.valueOf(month_id)}, null, null, null);
        if (cursor.getCount() <= 0)
            return total_expense;

        cursor.moveToFirst();

        do {
            total_expense = total_expense + cursor.getDouble(cursor.getColumnIndex(FIELD_AMOUNT));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return total_expense;
    }

    //returns the sum of all expenses
    public double getOverallTotalExpense() {
        double total_expense = 0;
        Cursor cursor = database.query(TABLE_EXPENSE, new String[]{FIELD_AMOUNT}, null, null, null, null, null);
        if (cursor.getCount() <= 0)
            return total_expense;

        cursor.moveToFirst();

        do {
            total_expense = total_expense + cursor.getDouble(cursor.getColumnIndex(FIELD_AMOUNT));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return total_expense;
    }

    //check if sheet already exists
    public boolean checkIfSheetExists(String month, String year) {
        Cursor cursor = database.query(TABLE_SHEETS, null, FIELD_MONTH + "=? AND " + FIELD_YEAR + "=?", new String[]{month, year}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    //converts sqlite boolean value from 0/1 to true/false
    private boolean getBooleanFormat(int x) {
        return x == 1;
    }

    //returns month name to corresponding month number
    private String getMonthName(String month) {
        String[] months = context.getResources().getStringArray(R.array.months);
        return months[Integer.parseInt(month)];
    }
}