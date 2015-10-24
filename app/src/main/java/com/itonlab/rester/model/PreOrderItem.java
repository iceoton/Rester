package com.itonlab.rester.model;

import android.content.ContentValues;
import android.database.Cursor;

public class PreOrderItem {
    private int id;
    private int menuId;
    //จำนวนของอาหารที่สั่งว่ากี่จาน เป็นต้น
    private int amount;
    //รสชาติเพิ่มเติม
    private String option;

    public static PreOrderItem newInstance(Cursor cursor){
        PreOrderItem preOrderItem = new PreOrderItem();
        preOrderItem.fromCursor(cursor);

        return preOrderItem;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt((cursor.getColumnIndexOrThrow(PreOrderTable.Columns._ID)));
        this.menuId = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._MENU_ID));
        this.amount = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._AMOUNT));
        this.option = cursor.getString(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._OPTION));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PreOrderTable.Columns._MENU_ID, menuId);
        values.put(PreOrderTable.Columns._AMOUNT, amount);
        values.put(PreOrderTable.Columns._OPTION, option);

        return  values;
    }

    public int getId() {
        return id;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getAmount() {
        return amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
