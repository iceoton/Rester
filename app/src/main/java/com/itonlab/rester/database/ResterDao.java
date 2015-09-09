package com.itonlab.rester.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itonlab.rester.model.FoodItem;
import com.itonlab.rester.model.FoodOrder;
import com.itonlab.rester.model.FoodOrderItem;
import com.itonlab.rester.model.MenuTable;
import com.itonlab.rester.model.OrderItemTable;
import com.itonlab.rester.model.OrderTable;
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

    public FoodItem getMenuAtId(int menuId){
        FoodItem foodItem = null;
        String sql = "SELECT * FROM menu WHERE id = ?";
        String[] selectionArgs = {String.valueOf(menuId)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            foodItem = FoodItem.newInstance(cursor);
        }

        return foodItem;
    }

    public void updateMenu(FoodItem foodItem){
        ContentValues values = new ContentValues();
        values.put(MenuTable.Columns._NAME_THAI, foodItem.getNameThai());
        values.put(MenuTable.Columns._PRICE, foodItem.getPrice());
        String[] whereArgs = {String.valueOf(foodItem.getId())};

        int affected = database.update(MenuTable.TABLE_NAME, values, "id=?", whereArgs);
        if(affected == 0){
            Log.d(TAG,"[Menu]update menu id " + foodItem.getId() + " not successful.");
        }

    }

    public void addToPreOrder(PreOrderItem preOrderItem){
        PreOrderItem olePreOrderItem = getPreOrderItemWithMenuID(preOrderItem.getMenuId());
        if(olePreOrderItem == null) {
            ContentValues values = preOrderItem.toContentValues();
            long insertIndex = database.insert(PreOrderTable.TABLE_NAME, null, values);
            if (insertIndex == -1) {
                Log.d(TAG, "An error occurred on inserting pre_order table.");
            } else {
                Log.d(TAG, "insert pre-order successful.");
            }
        } else {
            int newAmount = olePreOrderItem.getAmount() + preOrderItem.getAmount();
            updateAmountPreOrder(preOrderItem.getMenuId(), newAmount);
        }
    }

    private PreOrderItem getPreOrderItemWithMenuID(int menuID){
        PreOrderItem preOrderItem = null;
        String sql = "SELECT * FROM pre_order WHERE menu_id = ?";
        String[] whereArgs = {String.valueOf(menuID)};
        Cursor cursor = database.rawQuery(sql, whereArgs);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            preOrderItem = PreOrderItem.newInstance(cursor);
        }

        return preOrderItem;
    }

    public void updateAmountPreOrder(int menuID, int amountToUpdate){
        ContentValues values = new ContentValues();
        values.put(PreOrderTable.Columns._AMOUNT, amountToUpdate);
        String[] whereArgs = {String.valueOf(menuID)};

        int affected = database.update(PreOrderTable.TABLE_NAME, values, "menu_id=?", whereArgs);
        if(affected == 0){
            Log.d(TAG, "[PreOrder]update amount menu id " + menuID + " not successful.");
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

    public int addOrder(FoodOrder foodOrder){
        ContentValues values = foodOrder.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }

        return (int)insertIndex;
    }

    public void addOrderItem(FoodOrderItem foodOrderItem){
        ContentValues values = foodOrderItem.toContentValues();
        long insertIndex = database.insert(OrderItemTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order_item table.");
        } else {
            Log.d(TAG, "insert order_item successful.");
        }
    }

    public void clearPreOrder(){
        //Move data to Order table
        // 1. add order
        int totalOrderItem = 0;
        ArrayList<PreOrderItem> preOrderItems = getAllPreOrderItem();
        for (int i = 0; i < preOrderItems.size();i++){
            totalOrderItem += preOrderItems.get(i).getAmount();
        }
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setTotal(totalOrderItem);
        int orderId = addOrder(foodOrder);
        // 2. add order item
        for (PreOrderItem preOrderItem : preOrderItems){
            FoodOrderItem foodOrderItem = new FoodOrderItem();
            foodOrderItem.setOrderID(orderId);
            foodOrderItem.setMenuID(preOrderItem.getMenuId());
            foodOrderItem.setAmount(preOrderItem.getAmount());
            addOrderItem(foodOrderItem);
        }

        //Delete it out of Pre-Order table
        database.delete(PreOrderTable.TABLE_NAME, null, null);
    }

    public ArrayList<FoodOrder> getAllOrder(){
        ArrayList<FoodOrder> foodOrders = new ArrayList<FoodOrder>();
        String sql = "SELECT * FROM 'order'";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.getCount() > 0){
            FoodOrder foodOrder = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                foodOrder = foodOrder.newInstance(cursor);
                foodOrders.add(foodOrder);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + foodOrders.size());

        return foodOrders;
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
