package com.itonlab.rester.model;

import android.content.ContentValues;
import android.database.Cursor;

public class PreOrderItem {
    private int id;
    private String menuCode;
    //จำนวนของอาหารที่สั่งว่ากี่จาน เป็นต้น
    private int quantity;
    //รสชาติเพิ่มเติม
    private String option;
    private boolean ordered;
    private boolean served = false;
    private Status status = Status.UNDONE;

    public enum Status {
        UNDONE(0),
        DONE(1);


        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static PreOrderItem newInstance(Cursor cursor) {
        PreOrderItem preOrderItem = new PreOrderItem();
        preOrderItem.fromCursor(cursor);

        return preOrderItem;
    }

    public void fromCursor(Cursor cursor) {
        this.id = cursor.getInt((cursor.getColumnIndexOrThrow(PreOrderTable.Columns._ID)));
        this.menuCode = cursor.getString(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._MENU_CODE));
        this.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._QUANTITY));
        this.option = cursor.getString(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._OPTION));
        this.ordered = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._ORDERED)) == 1;
        this.served = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._SERVED)) == 1;
        int statusValue = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._STATUS));
        this.status = (statusValue == 1) ? Status.DONE : Status.UNDONE;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PreOrderTable.Columns._MENU_CODE, menuCode);
        values.put(PreOrderTable.Columns._QUANTITY, quantity);
        values.put(PreOrderTable.Columns._OPTION, option);
        values.put(PreOrderTable.Columns._ORDERED, (ordered ? 1 : 0));
        values.put(PreOrderTable.Columns._SERVED, (served ? 1 : 0));
        values.put(PreOrderTable.Columns._STATUS, status.getValue());

        return values;
    }

    public int getId() {
        return id;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
