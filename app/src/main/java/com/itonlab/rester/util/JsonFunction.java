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

    public String getStringJSONOrder(ArrayList<PreOrderItem> preOrderItems){
        JSONObject json = new JSONObject();
        try {
            json.put("ip", TCPUtils.getIP(mContext));
            json.put("name", appPreference.getYourName());

            JSONArray order = new JSONArray();
            for(PreOrderItem preOrderItem: preOrderItems) {
                JSONObject orderItem = new JSONObject();
                orderItem.put("id",preOrderItem.getMenuId());
                orderItem.put("amout", preOrderItem.getAmount());
                order.put(orderItem);
            }
            json.put("order",order);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
