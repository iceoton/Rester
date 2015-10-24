package com.itonlab.rester.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itonlab.rester.model.MenuItem;
import com.itonlab.rester.model.MenuTable;
import com.itonlab.rester.model.Order;
import com.itonlab.rester.model.OrderItem;
import com.itonlab.rester.model.OrderItemDetail;
import com.itonlab.rester.model.OrderItemTable;
import com.itonlab.rester.model.OrderTable;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.model.PreOrderTable;

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

    public ArrayList<MenuItem> getMenu() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        String sql = "SELECT * FROM menu";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            MenuItem menuItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                menuItem = MenuItem.newInstance(cursor);
                menuItems.add(menuItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of food in menu: " + menuItems.size());

        return menuItems;
    }

    public MenuItem getMenuAtId(int menuId) {
        MenuItem menuItem = null;
        String sql = "SELECT * FROM menu WHERE id = ?";
        String[] selectionArgs = {String.valueOf(menuId)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            menuItem = MenuItem.newInstance(cursor);
        }

        return menuItem;
    }

    public void updateMenu(MenuItem menuItem) {
        ContentValues values = new ContentValues();
        values.put(MenuTable.Columns._NAME_THAI, menuItem.getNameThai());
        values.put(MenuTable.Columns._PRICE, menuItem.getPrice());
        String[] whereArgs = {String.valueOf(menuItem.getId())};

        int affected = database.update(MenuTable.TABLE_NAME, values, "id=?", whereArgs);
        if(affected == 0){
            Log.d(TAG, "[Menu]update menu id " + menuItem.getId() + " not successful.");
        }

    }

    public void addPreOrderItem(PreOrderItem preOrderItem){
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

    public void removePreOrderItem(int itemId){
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(itemId)};
        database.delete(PreOrderTable.TABLE_NAME,whereClause, whereArgs);
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

    public int addOrder(Order order) {
        ContentValues values = order.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }

        return (int)insertIndex;
    }

    public void addOrderItem(OrderItem orderItem) {
        ContentValues values = orderItem.toContentValues();
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
        Order order = new Order();
        order.setTotal(totalOrderItem);
        int orderId = addOrder(order);
        // 2. add order item
        for (PreOrderItem preOrderItem : preOrderItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderID(orderId);
            orderItem.setMenuID(preOrderItem.getMenuId());
            orderItem.setAmount(preOrderItem.getAmount());
            addOrderItem(orderItem);
        }

        //Delete it out of Pre-Order table
        database.delete(PreOrderTable.TABLE_NAME, null, null);
    }

    public ArrayList<Order> getAllOrder() {
        ArrayList<Order> orders = new ArrayList<Order>();
        String sql = "SELECT * FROM 'order' ORDER BY order_time DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.getCount() > 0){
            Order order = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                order = order.newInstance(cursor);
                orders.add(order);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + orders.size());

        return orders;
    }

    public ArrayList<OrderItemDetail> getOrderDetail(int orderId) {
        ArrayList<OrderItemDetail> orderItemDetails = new ArrayList<OrderItemDetail>();
        String sql = "SELECT menu_id, name_th, price, amount" +
                " FROM order_item INNER JOIN menu ON menu_id = menu.id"
                +" WHERE order_id = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        Cursor cursor = database.rawQuery(sql,whereArgs);

        if(cursor.getCount() > 0){
            OrderItemDetail orderItemDetail = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                orderItemDetail = new OrderItemDetail();
                orderItemDetail.setMenuId(cursor.getInt(0));
                orderItemDetail.setName(cursor.getString(1));
                orderItemDetail.setPrice(cursor.getDouble(2));
                orderItemDetail.setAmount(cursor.getInt(3));
                orderItemDetails.add(orderItemDetail);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in order: " + orderItemDetails.size());

        return orderItemDetails;
    }

    public ArrayList<OrderItemDetail> getSummaryPreOrder() {
        ArrayList<OrderItemDetail> orderItemDetails = new ArrayList<OrderItemDetail>();
        String sql = "SELECT menu_id, name_th, price, amount, pre_order.id" +
                " FROM pre_order INNER JOIN menu ON menu_id = menu.id";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            OrderItemDetail orderItemDetail = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                orderItemDetail = new OrderItemDetail();
                orderItemDetail.setMenuId(cursor.getInt(0));
                orderItemDetail.setName(cursor.getString(1));
                orderItemDetail.setPrice(cursor.getDouble(2));
                orderItemDetail.setAmount(cursor.getInt(3));
                orderItemDetail.setPreOderId(cursor.getInt(4));
                orderItemDetails.add(orderItemDetail);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in summary: " + orderItemDetails.size());

        return orderItemDetails;
    }



}
