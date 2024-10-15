package com.example.parking5.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "account_preference.db";

    // Table name
    private static final String TABLE_NAME = "ip_preference";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_VPN = "vpn";

    // SQL to create table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_IP + " TEXT UNIQUE,"
            + COLUMN_VPN + " INTEGER)";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create table again
        onCreate(db);
    }
}