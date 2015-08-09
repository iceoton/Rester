package com.itonlab.rester.model;

import android.database.sqlite.SQLiteDatabase;

public class PreOrderTable {
    public static final String TABLE_NAME = "pre_order";

    public static class Columns{
        public Columns(){}

        public static final String _ID = "id";
        public static final String _MENU_ID = "menu_id";
        public static final String _AMOUNT = "amount";
    }

    public static void onCreate(SQLiteDatabase database) {
        final String CREATE_PLACE = "CREATE TABLE " + TABLE_NAME
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, menu_id INTEGER NOT NULL," +
                " amount INTEGER NOT NULL);";
        database.execSQL(CREATE_PLACE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
