package com.itonlab.rester.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itonlab.rester.model.FoodItem;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.model.PreOrderTable;
import com.itonlab.rester.model.SummaryItem;

import java.util.ArrayList;

public class ResterDao {
    private static final String TAG = "DATABASE";
    private Context mContext;
    private SQLiteDatabase database;
    private ResterOpenHelper openHelper;

    public ResterDao(Context context) {
        this.mContext = context;
        openHelper = new ResterOpenHelper(context);
    }

    public void open() throws SQLException {
        database = openHelper.getWritableDatabase();
    }

    public void close() {
        openHelper.close();
    }

    public ArrayList<FoodItem> getMenu(){
        ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
        String sql = "SELECT * FROM menu";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            FoodItem foodItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                foodItem = FoodItem.newInstance(cursor);
                foodItems.add(foodItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of food in menu: " + foodItems.size());

        return  foodItems;
    }

    public void addToPreOrder(PreOrderItem preOrderItem){
        ContentValues values = preOrderItem.toContentValues();
        long insertIndex = database.insert(PreOrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting pre_order table.");
        } else {
            Log.d(TAG, "insert pre-order successful.");
        }
    }

    public ArrayList<PreOrderItem> getAllPreOrderItem(){
        ArrayList<PreOrderItem> preOrderItems = new ArrayList<PreOrderItem>();
        String sql = "SELECT * FROM pre_order";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            PreOrderItem preOrderItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                preOrderItem = PreOrderItem.newInstance(cursor);
                preOrderItems.add(preOrderItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in pre_order: " + preOrderItems.size());

        return preOrderItems;
    }

    public ArrayList<SummaryItem> getSummaryOrder(){
        ArrayList<SummaryItem> summaryItems = new ArrayList<SummaryItem>();
        String sql = "SELECT menu_id, name_th, price, amount FROM pre_order INNER JOIN menu ON menu_id = menu.id";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            SummaryItem summaryItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                summaryItem = new SummaryItem();
                summaryItem.setMenuId(cursor.getInt(0));
                summaryItem.setName(cursor.getString(1));
                summaryItem.setPrice(cursor.getDouble(2));
                summaryItem.setAmount(cursor.getInt(3));
                summaryItems.add(summaryItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in summary: " + summaryItems.size());

        return summaryItems;
    }

}
