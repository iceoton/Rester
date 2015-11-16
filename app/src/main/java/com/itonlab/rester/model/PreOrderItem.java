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

    public static PreOrderItem newInstance(Cursor cursor){
        PreOrderItem preOrderItem = new PreOrderItem();
        preOrderItem.fromCursor(cursor);

        return preOrderItem;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt((cursor.getColumnIndexOrThrow(PreOrderTable.Columns._ID)));
        this.menuCode = cursor.getString(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._MENU_CODE));
        this.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._QUANTITY));
        this.option = cursor.getString(cursor.getColumnIndexOrThrow(PreOrderTable.Columns._OPTION));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PreOrderTable.Columns._MENU_CODE, menuCode);
        values.put(PreOrderTable.Columns._QUANTITY, quantity);
        values.put(PreOrderTable.Columns._OPTION, option);

        return  values;
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

}
