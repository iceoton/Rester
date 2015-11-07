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

    public String getStringJSONOrder(ArrayList<PreOrderItem> preOrderItems, double totalPrice){
        JSONObject json = new JSONObject();
        try {
            json.put("ip", TCPUtils.getIP(mContext));
            json.put("name", appPreference.getYourName());
            int total = 0;
            JSONArray order = new JSONArray();
            for(PreOrderItem preOrderItem: preOrderItems) {
                total += preOrderItem.getAmount();
                JSONObject orderItem = new JSONObject();
                orderItem.put("id",preOrderItem.getMenuId());
                orderItem.put("amount", preOrderItem.getAmount());
                orderItem.put("option", preOrderItem.getOption());
                order.put(orderItem);
            }
            json.put("total", total);
            json.put("total_price", totalPrice);
            json.put("order",order);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
