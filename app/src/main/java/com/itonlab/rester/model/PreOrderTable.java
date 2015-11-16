package com.itonlab.rester.model;

import android.database.sqlite.SQLiteDatabase;

public class PreOrderTable {
    public static final String TABLE_NAME = "pre_order";

    public static class Columns{
        public Columns(){}

        public static final String _ID = "id";
        public static final String _MENU_CODE = "menu_code";
        public static final String _QUANTITY = "quantity";
        public static final String _OPTION = "option";
    }

    public static void onCreate(SQLiteDatabase database) {
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}
