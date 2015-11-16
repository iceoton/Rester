package com.itonlab.rester.model;

import android.content.ContentValues;
import android.database.Cursor;

public class OrderItem {
    private int id;
    private int orderID;
    private String menuCode;
    private int quantity;
    private String option;
    private boolean served;

    public static OrderItem newInstance(Cursor cursor) {
        OrderItem orderItem = new OrderItem();
        orderItem.fromCursor(cursor);

        return orderItem;
    }

    public void fromCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._ID));
        this.orderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._ORDER_ID));
        this.menuCode = cursor.getString(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._MENU_CODE));
        this.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._QUANTITY));
        this.option = cursor.getString(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._OPTION));
        this.served = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._SERVED)) == 1;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(OrderItemTable.Columns._ORDER_ID, this.orderID);
        values.put(OrderItemTable.Columns._MENU_CODE, this.menuCode);
        values.put(OrderItemTable.Columns._QUANTITY, this.quantity);
        values.put(OrderItemTable.Columns._OPTION, this.option);
        int servedValue = 0;
        if (served) {
            servedValue = 1;
        }
        values.put(OrderItemTable.Columns._SERVED, servedValue);

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
