package com.shineunyoon.stockwatch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockTB";

    ///DB Columns
    private static final String SYMBOL = "Symbol";
    private static final String COMPANY_NAME = "Company_Name";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( " + SYMBOL + " TEXT not null unique, " + COMPANY_NAME +" TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler (MainActivity context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY_NAME, stock.company_name);
        database.insert(TABLE_NAME, null, values);
    }

    public ArrayList<String[]> loadStocks() {
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, new String[]{SYMBOL, COMPANY_NAME}, null, null, null , null, null);
        if (cursor != null){
            cursor.moveToFirst();

            for (int i =0; i<cursor.getCount(); i++){
                String symbol = cursor.getString(0);
                String company_name = cursor.getString(1);
                stocks.add(new String[] {symbol, company_name});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void deleteStock(String symbol) {
        int cnt = database.delete(TABLE_NAME, "SYMBOL = ?", new String[] { symbol });
    }

    public void shutDown() {
        database.close();
    }
}
