package com.itonlab.rester.util;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.Order;
import com.itonlab.rester.model.OrderItem;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.model.PreOrderTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.TCPUtils;

public class JsonFunction {
    private AppPreference appPreference;
    private Context mContext;

    public JsonFunction(Context context) {
        appPreference = new AppPreference(context);
        this.mContext = context;
    }

    public static class Message {
        public enum Type {
            PAY_CONFIRM_MESSAGE("pay_confirm_ms"),
            ORDER_STATUS_MESSAGE("order_status_ms"),
            EDIT_ORDER_MESSAGE("edit_order_ms");

            Type(String key) {
                this.jsonKey = key;
            }

            public String getJsonKey() {
                return this.jsonKey;
            }

            private String jsonKey;
        }

        private Type messageType;
        private String fromIP;
        private JSONObject jsonBody;

        public Type getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageTypeKey) {
            try {
                if (messageTypeKey.equals(Type.PAY_CONFIRM_MESSAGE.getJsonKey())) {
                    this.messageType = Type.PAY_CONFIRM_MESSAGE;
                } else if (messageTypeKey.equals(Type.ORDER_STATUS_MESSAGE.getJsonKey())) {
                    this.messageType = Type.ORDER_STATUS_MESSAGE;
                } else if (messageTypeKey.equals(Type.EDIT_ORDER_MESSAGE.getJsonKey())) {
                    this.messageType = Type.EDIT_ORDER_MESSAGE;
                } else {
                    throw new JSONException("Message type key can't accepted.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getFromIP() {
            return fromIP;
        }

        public void setFromIP(String fromIP) {
            this.fromIP = fromIP;
        }

        public JSONObject getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    public static Message acceptMessage(String json) {
        Log.d("JSON", json);
        Message message = new Message();
        try {
            JSONObject jsonObject = new JSONObject(json);

            message.setMessageType(jsonObject.getString("message_type"));
            message.setFromIP(jsonObject.getString("from_ip"));
            message.setJsonBody(jsonObject.getJSONObject("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public void decideWhatToDo(Message message) {
        switch (message.getMessageType()) {
            case PAY_CONFIRM_MESSAGE:
                acceptPayConfirm(message);
                break;
            case ORDER_STATUS_MESSAGE:
                acceptOrderStatus(message);
                break;
            case EDIT_ORDER_MESSAGE:
                acceptEditOrder(message);
                break;
            default:
                Log.d("JSON", "Do nothing");
        }
    }

    private void acceptPayConfirm(Message message) {
        AppPreference appPreference = new AppPreference(mContext);
        String masterIP = appPreference.getMasterIP();
        ResterDao databaseDao = new ResterDao(mContext);
        databaseDao.open();
        if (masterIP.equals(message.getFromIP())) {
            //Move data to Order table
            ArrayList<PreOrderItem> preOrderItems = databaseDao.getAllPreOrderItemOrdered();
            // 1. add order
            int totalOrderItem = 0;
            for (int i = 0; i < preOrderItems.size(); i++) {
                totalOrderItem += preOrderItems.get(i).getQuantity();
            }
            Order order = new Order();
            order.setTotalQuantity(totalOrderItem);
            order.setTake(appPreference.getTakeOrder());
            int orderId = databaseDao.addOrder(order);
            // 2. add order item
            for (PreOrderItem preOrderItem : preOrderItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderID(orderId);
                orderItem.setMenuCode(preOrderItem.getMenuCode());
                orderItem.setQuantity(preOrderItem.getQuantity());
                orderItem.setOption(preOrderItem.getOption());
                orderItem.setServed(true);
                databaseDao.addOrderItem(orderItem);
            }
            // 3. clear pre-order table
            databaseDao.clearPreOrder();
        }
        databaseDao.close();
    }

    private void acceptOrderStatus(Message message) {
        AppPreference appPreference = new AppPreference(mContext);
        String masterIP = appPreference.getMasterIP();
        ResterDao databaseDao = new ResterDao(mContext);
        databaseDao.open();

        if (masterIP.equals(message.getFromIP())) {
            //update pre_order with id
            JSONObject body = message.getJsonBody();
            ContentValues values = new ContentValues();
            try {
                int preOrderId = body.getInt("pre_id");
                values.put(PreOrderTable.Columns._SERVED, body.getInt("served"));
                values.put(PreOrderTable.Columns._STATUS, body.getInt("status"));
                databaseDao.updatePreOrderByValues(preOrderId, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        databaseDao.close();
    }

    private void acceptEditOrder(Message message) {
        AppPreference appPreference = new AppPreference(mContext);
        String masterIP = appPreference.getMasterIP();
        ResterDao databaseDao = new ResterDao(mContext);
        databaseDao.open();

        if (masterIP.equals(message.getFromIP())) {
            //update pre_order with id
            JSONObject body = message.getJsonBody();
            ContentValues values = new ContentValues();
            try {
                int preOrderId = body.getInt("pre_id");
                values.put(PreOrderTable.Columns._QUANTITY, body.getInt(PreOrderTable.Columns._QUANTITY));
                values.put(PreOrderTable.Columns._OPTION, body.getString(PreOrderTable.Columns._OPTION));
                databaseDao.updatePreOrderByValues(preOrderId, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        databaseDao.close();
    }

    public String getJSONOrderMessage(ArrayList<PreOrderItem> preOrderItems, double totalPrice) {

        JSONObject message = new JSONObject();
        try {
            message.put("message_type", "order_ms");
            message.put("from_ip", TCPUtils.getIP(mContext));
            //prepare body for add to message
            JSONObject messageBody = new JSONObject();
            messageBody.put("name", appPreference.getYourName());
            int total_quantity = 0;
            JSONArray order = new JSONArray();
            for (PreOrderItem preOrderItem : preOrderItems) {
                total_quantity += preOrderItem.getQuantity();
                JSONObject orderItem = new JSONObject();
                orderItem.put("pre_id", preOrderItem.getId());
                orderItem.put("menu_code", preOrderItem.getMenuCode());
                orderItem.put("quantity", preOrderItem.getQuantity());
                orderItem.put("option", preOrderItem.getOption());
                order.put(orderItem);
            }
            messageBody.put("total_quantity", total_quantity);
            messageBody.put("total_price", totalPrice);
            messageBody.put("take", appPreference.getTakeOrder().getValue());
            messageBody.put("order", order);
            // add body to message
            message.put("body", messageBody);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();
    }

}
