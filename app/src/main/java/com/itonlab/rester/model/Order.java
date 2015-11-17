package com.itonlab.rester.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Order {
    private int id;
    private int totalQuantity;
    private Date orderTime;
    private boolean served;
    private Take take;

    public enum Take {
        HERE(0),
        HOME(1);

        private final int value;

        Take(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static Order newInstance(Cursor cursor) {
        Order order = new Order();
        order.fromCursor(cursor);

        return order;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._ID));
        this.totalQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL_QUANTITY));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._ORDER_TIME));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            this.orderTime = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.served = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._SERVED)) == 1;
        int takeValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TAKE));
        switch (takeValue) {
            case 1:
                this.take = Take.HOME;
                break;
            default:
                this.take = Take.HERE;
        }
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderTable.Columns._TOTAL_QUANTITY, this.totalQuantity);
        values.put(OrderTable.Columns._TAKE, this.take.getValue());
        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    public Take getTake() {
        return take;
    }

    public void setTake(Take take) {
        this.take = take;
    }
}
