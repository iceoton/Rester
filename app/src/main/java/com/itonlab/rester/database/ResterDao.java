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
import com.itonlab.rester.model.Picture;
import com.itonlab.rester.model.PictureTable;
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
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            MenuItem menuItem;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
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

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            menuItem = MenuItem.newInstance(cursor);
        }

        return menuItem;
    }

    public MenuItem getMenuByCode(String menuCode) {
        MenuItem menuItem = null;
        String sql = "SELECT * FROM menu WHERE code = ?";
        String[] selectionArgs = {String.valueOf(menuCode)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            menuItem = MenuItem.newInstance(cursor);
        }

        return menuItem;
    }

    public void addMenu(MenuItem menuItem) {
        ContentValues values = menuItem.toContentValues();
        long insertIndex = database.insert(MenuTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting menu table.");
        } else {
            Log.d(TAG, "insert menu successful.");
        }
    }

    public void deleteMenu(int menuId) {
        // delete its picture
        int pictureId = getMenuAtId(menuId).getPictureId();
        deleteMenuPicture(pictureId);
        // and last, delete it
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(menuId)};
        database.delete(MenuTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void updateMenu(MenuItem menuItem) {
        ContentValues values = menuItem.toContentValues();
        String[] whereArgs = {String.valueOf(menuItem.getId())};

        int affected = database.update(MenuTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[Menu]update menu id " + menuItem.getId() + " not successful.");
        }

    }

    public Picture getMenuPicture(int pictureId) {
        String sql = "SELECT * FROM picture WHERE id=?";
        String[] selectionArgs = {String.valueOf(pictureId)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        Picture picture = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            picture = Picture.newInstance(cursor);
        }
        cursor.close();


        return picture;
    }

    public int addMenuPicture(Picture picture) {
        ContentValues values = picture.toContentValues();
        long insertIndex = database.insert(PictureTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting picture table.");
        } else {
            Log.d(TAG, "insert picture successful.");
        }

        return (int) insertIndex;
    }

    public void updateMenuPicture(Picture picture) {
        ContentValues values = picture.toContentValues();
        String[] whereArgs = {String.valueOf(picture.getId())};

        int affected = database.update(PictureTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[Menu]update menu id " + picture.getId() + " not successful.");
        }
    }

    public void deleteMenuPicture(int pictureId) {
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(pictureId)};
        database.delete(PictureTable.TABLE_NAME, whereClause, whereArgs);
    }

    public ArrayList<PreOrderItem> getAllPreOrderItem() {
        ArrayList<PreOrderItem> preOrderItems = new ArrayList<PreOrderItem>();
        String sql = "SELECT * FROM pre_order";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            PreOrderItem preOrderItem;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                preOrderItem = PreOrderItem.newInstance(cursor);
                preOrderItems.add(preOrderItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in pre_order: " + preOrderItems.size());

        return preOrderItems;
    }

    public ArrayList<PreOrderItem> getPreOrderItemNotOrdered() {
        ArrayList<PreOrderItem> preOrderItems = new ArrayList<PreOrderItem>();
        String sql = "SELECT * FROM pre_order WHERE ordered=0";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            PreOrderItem preOrderItem;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                preOrderItem = PreOrderItem.newInstance(cursor);
                preOrderItems.add(preOrderItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in pre_order: " + preOrderItems.size());

        return preOrderItems;
    }

    public void addPreOrderItem(PreOrderItem preOrderItem) {
        ContentValues values = preOrderItem.toContentValues();
        long insertIndex = database.insert(PreOrderTable.TABLE_NAME, null, values);

        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting pre_order table.");
        } else {
            Log.d(TAG, "insert pre-order successful.");
        }
    }

    public void removePreOrderItem(int itemId) {
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(itemId)};
        database.delete(PreOrderTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void clearPreOrder() {
        //Delete all row out of Pre-Order table
        database.delete(PreOrderTable.TABLE_NAME, null, null);
    }

    public void updatePreOrder(PreOrderItem preOrderItem) {
        ContentValues values = new ContentValues();
        values.put(PreOrderTable.Columns._QUANTITY, preOrderItem.getQuantity());
        values.put(PreOrderTable.Columns._OPTION, preOrderItem.getOption());
        String[] whereArgs = {String.valueOf(preOrderItem.getId())};

        int affected = database.update(PreOrderTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[PreOrder]update quantity pre-order id " + preOrderItem.getId()
                    + " not successful.");
        }
    }

    public void updatePreOderToOrdered(ArrayList<PreOrderItem> preOrderItems) {
        ContentValues values;

        for (PreOrderItem preOrderItem : preOrderItems) {
            values = new ContentValues();
            values.put(PreOrderTable.Columns._ORDERED, 1);
            String[] whereArgs = {String.valueOf(preOrderItem.getId())};

            int affected = database.update(PreOrderTable.TABLE_NAME, values, "id=?", whereArgs);
            if (affected == 0) {
                Log.d(TAG, "[PreOrder]update ordered pre-order id " + preOrderItem.getId()
                        + " not successful.");
            }
        }
    }

    public int addOrder(Order order) {
        ContentValues values = order.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }

        return (int) insertIndex;
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

    public ArrayList<Order> getAllOrder() {
        ArrayList<Order> orders = new ArrayList<Order>();
        String sql = "SELECT * FROM 'order' ORDER BY order_time DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            Order order;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                order = Order.newInstance(cursor);
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
        String sql = "SELECT menu_code, name_th, price, quantity, option" +
                " FROM order_item INNER JOIN menu ON menu_code = menu.code"
                + " WHERE order_id = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        Cursor cursor = database.rawQuery(sql, whereArgs);

        if (cursor.getCount() > 0) {
            OrderItemDetail orderItemDetail;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                orderItemDetail = new OrderItemDetail();
                orderItemDetail.setOrdered(true);
                orderItemDetail.setMenuCode(cursor.getString(0));
                orderItemDetail.setName(cursor.getString(1));
                orderItemDetail.setPrice(cursor.getDouble(2));
                orderItemDetail.setQuantity(cursor.getInt(3));
                orderItemDetail.setOption(cursor.getString(4));
                orderItemDetails.add(orderItemDetail);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in order: " + orderItemDetails.size());

        return orderItemDetails;
    }

    public ArrayList<OrderItemDetail> getPreOrderDetail() {
        ArrayList<OrderItemDetail> orderItemDetails = new ArrayList<OrderItemDetail>();
        String sql = "SELECT menu_code, name_th, price, quantity, option, pre_order.id, ordered" +
                " FROM pre_order INNER JOIN menu ON menu_code = menu.code ORDER BY ordered";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            OrderItemDetail orderItemDetail;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                orderItemDetail = new OrderItemDetail();
                orderItemDetail.setMenuCode(cursor.getString(0));
                orderItemDetail.setName(cursor.getString(1));
                orderItemDetail.setPrice(cursor.getDouble(2));
                orderItemDetail.setQuantity(cursor.getInt(3));
                orderItemDetail.setOption(cursor.getString(4));
                orderItemDetail.setPreOderId(cursor.getInt(5));
                orderItemDetail.setOrdered(cursor.getInt(6) == 1);// true when ordered equal 1
                orderItemDetails.add(orderItemDetail);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in summary: " + orderItemDetails.size());

        return orderItemDetails;
    }


}
