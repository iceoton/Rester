package com.itonlab.rester.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Order {
    private int id;
    private int total;
    private Date orderTime;
    private boolean served;

    public static Order newInstance(Cursor cursor) {
        Order order = new Order();
        order.fromCursor(cursor);

        return order;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._ID));
        this.total = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._ORDER_TIME));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            this.orderTime = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._SERVED)) == 1) {
            this.served = true;
        } else {
            this.served = false;
        }
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderTable.Columns._TOTAL, this.total);

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}
