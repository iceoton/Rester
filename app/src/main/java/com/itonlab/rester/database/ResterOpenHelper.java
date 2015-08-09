package com.itonlab.rester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itonlab.rester.model.PreOrderTable;

public class ResterOpenHelper extends SQLiteOpenHelper{
    private static final String TAG = "DATABASE";
    private static final String DATABASE_NAME = "rester.db";
    private static final int DATABASE_VERSION = 1;

    public ResterOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        PreOrderTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PreOrderTable.onUpgrade(db, oldVersion, newVersion);
    }
}
