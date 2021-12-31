package com.fm.egecuzdan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.db.models.GiderModel;
import com.fm.egecuzdan.db.models.SheetModel;

import java.util.ArrayList;

public class GiderDB extends SQLiteOpenHelper {

    private static String DB_ISIM = "expense_calc.db";
    private static int DB_VERSIYON = 1;

    //expense tablosu için
    private static final String GİDER_TABLO = "expense";
    private static final String ALAN_ID = "id_expense";
    private static final String ALAN_AÇIKLAMA = "remarks";
    private static final String ALAN_MIKTAR = "amount";
    private static final String ALAN_TARIH = "date";
    private static final String ALAN_DÜZENLİ_ÖDEMEDİR = "is_regular";

    //foreign key sheet tablosunun ay id'sini referans ediyor
    private static final String ALAN_AY_ID = "id_month";

    //sheets tablosu için
    private static final String SHEETS_TABLO = "sheets";
    private static final String ALAN_AY = "month";
    private static final String ALAN_YIL = "year";
    private static final String ALAN_GELIR = "income";

    private Context context;
    private SQLiteDatabase database;

    private static final String GİDER_TABLO_OLUSTUR = "create table if not exists " + GİDER_TABLO
            + " ("
            + ALAN_ID + " integer primary key autoincrement,"
            + ALAN_AÇIKLAMA + " text,"
            + ALAN_MIKTAR + " double not null,"
            + ALAN_DÜZENLİ_ÖDEMEDİR + " text not null,"
            + ALAN_TARIH + " int not null,"
            + ALAN_AY_ID + " integer not null,"
            + " FOREIGN KEY (" + ALAN_AY_ID + ") REFERENCES " + SHEETS_TABLO + "(" + ALAN_AY_ID + ")"
            + ")";

    private static final String TABLE_SHEETS_OLUSTUR = "create table if not exists " + SHEETS_TABLO
            + " ("
            + ALAN_AY_ID + " integer primary key autoincrement,"
            + ALAN_AY + " int not null,"
            + ALAN_YIL + " int not null,"
            + ALAN_GELIR + " double not null"
            + ")";

    public GiderDB(Context context) {
        super(context, DB_ISIM, null, DB_VERSIYON);
        this.context = context;
        this.database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SHEETS_OLUSTUR);
        db.execSQL(GİDER_TABLO_OLUSTUR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GİDER_TABLO);
        db.execSQL("DROP TABLE IF EXISTS " + SHEETS_TABLO);
        onCreate(db);
    }

    //add a new sheet in sheets table
    public boolean yeniSheetEkle(String ay, String yıl, double gelir) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALAN_AY, ay);
        contentValues.put(ALAN_YIL, yıl);
        contentValues.put(ALAN_GELIR, gelir);
        if (checkIfSheetExists(ay, yıl)) {
            database.insert(SHEETS_TABLO, null, contentValues);
            database.close();
            return true;
        } else {
            return false;
        }
    }

    //expense tablodan bütün giderlerin listesi döndürülüyor
    public ArrayList<GiderModel> getGiderler(String ay_id) {
        ArrayList<GiderModel> giderModelList = new ArrayList<>();
        Cursor cursor = database.query(GİDER_TABLO, null, ALAN_AY_ID + "=?", new String[]{ay_id}, null, null, ALAN_TARIH);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            GiderModel expenseMode = new GiderModel();
            expenseMode.setId(cursor.getInt(cursor.getColumnIndex(ALAN_ID)));
            expenseMode.setAçıklama(cursor.getString(cursor.getColumnIndex(ALAN_AÇIKLAMA)));
            expenseMode.setMiktar(cursor.getString(cursor.getColumnIndex(ALAN_MIKTAR)));
            expenseMode.setTarih(cursor.getString(cursor.getColumnIndex(ALAN_TARIH)));
            expenseMode.setDüzenliÖdemedir(getBooleanFormat(cursor.getInt(cursor.getColumnIndex(ALAN_DÜZENLİ_ÖDEMEDİR))));
            giderModelList.add(expenseMode);
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return giderModelList;
    }

    //belirli bir ay için sheet verisini döndürme
    public SheetModel getGelirData(String month_id) {
        SheetModel sheetModel = new SheetModel();
        Cursor cursor = database.query(SHEETS_TABLO, null, ALAN_AY_ID + "=?", new String[]{month_id}, null, null, null);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            sheetModel.setId(cursor.getInt(cursor.getColumnIndex(ALAN_AY_ID)));
            sheetModel.setGelir(cursor.getDouble(cursor.getColumnIndex(ALAN_GELIR)));
            sheetModel.setAy(getMonthName(cursor.getString(cursor.getColumnIndex(ALAN_AY))));
            sheetModel.setYıl(cursor.getString(cursor.getColumnIndex(ALAN_YIL)));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return sheetModel;
    }

    //sheets tablosunda "gelir"i düzenleme
    public void editGelir(double income, String month_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALAN_GELIR, income);
        String[] args = {month_id};
        database.update(SHEETS_TABLO, contentValues, ALAN_AY_ID + "=?", args);
        database.close();
    }

    //gider girişini belirli id ile düzenleme
    public void updateGider(String miktar, String remarks, String tarih, boolean bool, int secili_expense_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALAN_AÇIKLAMA, remarks);
        contentValues.put(ALAN_MIKTAR, miktar);
        contentValues.put(ALAN_TARIH, tarih);
        contentValues.put(ALAN_DÜZENLİ_ÖDEMEDİR, bool);
        String[] args = {String.valueOf(secili_expense_id)};
        database.update(GİDER_TABLO, contentValues, ALAN_ID + "=?", args);
        database.close();
    }

    // sheet tablosundan bütün sheetlerin listesini döndürme
    public ArrayList<SheetModel> getSheets() {
        ArrayList<SheetModel> sheetModelList = new ArrayList<>();
        Cursor cursor = database.query(SHEETS_TABLO, null, null, null, null, null, ALAN_YIL + " , " + ALAN_AY);
        if (cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        do {
            SheetModel sheetModel = new SheetModel();
            sheetModel.setId(cursor.getInt(cursor.getColumnIndex(ALAN_AY_ID)));
            sheetModel.setGelir(cursor.getDouble(cursor.getColumnIndex(ALAN_GELIR)));
            sheetModel.setAy(getMonthName(cursor.getString(cursor.getColumnIndex(ALAN_AY))));
            sheetModel.setYıl(cursor.getString(cursor.getColumnIndex(ALAN_YIL)));
            sheetModelList.add(sheetModel);
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return sheetModelList;
    }

    //gider tablosuna yeni gider ekleme
    public void giderEkle(String amount, String remarks, String date, boolean bool, String sel_month_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALAN_AÇIKLAMA, remarks);
        contentValues.put(ALAN_MIKTAR, amount);
        contentValues.put(ALAN_TARIH, date);
        contentValues.put(ALAN_DÜZENLİ_ÖDEMEDİR, bool);
        contentValues.put(ALAN_AY_ID, sel_month_id);
        database.insert(GİDER_TABLO, null, contentValues);
        database.close();
    }

    //expense'i id ile silme
    public void giderSil(long expense_id) {
        database.delete(GİDER_TABLO, ALAN_ID + "=?", new String[]{String.valueOf(expense_id)});
        database.close();
    }

    //belirli ay için toplam expense döndürme
    public double getAylıkGider(int ay_id) {
        double toplam_expense = 0;
        Cursor cursor = database.query(GİDER_TABLO, new String[]{ALAN_MIKTAR}, ALAN_AY_ID + "=?", new String[]{String.valueOf(ay_id)}, null, null, null);
        if (cursor.getCount() <= 0)
            return toplam_expense;

        cursor.moveToFirst();

        do {
            toplam_expense = toplam_expense + cursor.getDouble(cursor.getColumnIndex(ALAN_MIKTAR));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return toplam_expense;
    }

    //bütün expense'lerin toplamını döndürme
    public double getTotalGider() {
        double total_gider = 0;
        Cursor cursor = database.query(GİDER_TABLO, new String[]{ALAN_MIKTAR}, null, null, null, null, null);
        if (cursor.getCount() <= 0)
            return total_gider;

        cursor.moveToFirst();

        do {
            total_gider = total_gider + cursor.getDouble(cursor.getColumnIndex(ALAN_MIKTAR));
        } while (cursor.moveToNext());

        cursor.close();
        database.close();
        return total_gider;
    }

    //sheet halihazırda var mı
    public boolean checkIfSheetExists(String month, String year) {
        Cursor cursor = database.query(SHEETS_TABLO, null, ALAN_AY + "=? AND " + ALAN_YIL + "=?", new String[]{month, year}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    //sqlite boolean değerini 0/1'den true/false'a çevirme
    private boolean getBooleanFormat(int x) {
        return x == 1;
    }

    //ay ismini eşleşen ay numarasına döndürme
    private String getMonthName(String month) {
        String[] months = context.getResources().getStringArray(R.array.aylar);
        return months[Integer.parseInt(month)];
    }
}