package com.itonlab.rester.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.itonlab.rester.R;
import com.itonlab.rester.model.Order;

public class AppPreference {
    private Context mContext;
    SharedPreferences sharedPref;

    public AppPreference(Context mContext) {
        this.mContext = mContext;
        sharedPref = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void saveMasterIP(String ip) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("masterIP", ip);
        editor.apply();
    }

    public String getMasterIP() {
        return sharedPref.getString("masterIP", mContext.getResources().getString(R.string.default_ip_address));
    }

    public void saveClientIP(String ip) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("clientIP", ip);
        editor.apply();
    }

    public String getClientIP() {
        return sharedPref.getString("clientIP", mContext.getResources().getString(R.string.default_ip_address));
    }

    public void saveYourName(String name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("yourName", name);
        editor.apply();
    }

    public String getYourName() {
        return sharedPref.getString("yourName", mContext.getResources().getString(R.string.app_name));
    }

    public void saveAppLanguage(String language) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appLanguage", language);
        editor.apply();
    }

    public String getAppLanguage() {
        return sharedPref.getString("appLanguage", "en");
    }

    public void saveAppPassword(String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("appPassword", password);
        editor.apply();
    }

    public String getAppPassword() {
        return sharedPref.getString("appPassword", "1234");
    }

    public void saveTakeOrder(Order.Take take) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("takeOrder", take.getValue());
        editor.apply();
    }

    public Order.Take getTakeOrder() {
        int takeValue = sharedPref.getInt("takeOrder", 0);
        Order.Take take = (takeValue == 0) ? Order.Take.HERE : Order.Take.HOME;
        return take;
    }
}
