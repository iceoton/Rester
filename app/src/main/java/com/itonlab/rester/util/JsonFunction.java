package com.itonlab.rester.util;

import android.content.Context;

import com.itonlab.rester.model.PreOrderItem;

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

    public String getJSONOrderMessage(ArrayList<PreOrderItem> preOrderItems, double totalPrice) {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", "order_ms");
            message.put("from_ip", TCPUtils.getIP(mContext));
            //prepare body for add to message
            JSONObject messageBody = new JSONObject();
            messageBody.put("name", appPreference.getYourName());
            int total = 0;
            JSONArray order = new JSONArray();
            for(PreOrderItem preOrderItem: preOrderItems) {
                total += preOrderItem.getAmount();
                JSONObject orderItem = new JSONObject();
                orderItem.put("menu_id", preOrderItem.getMenuId());
                orderItem.put("amount", preOrderItem.getAmount());
                orderItem.put("option", preOrderItem.getOption());
                order.put(orderItem);
            }
            messageBody.put("total", total);
            messageBody.put("total_price", totalPrice);
            messageBody.put("order", order);
            // add body to message
            message.put("body", messageBody);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();
    }
}
