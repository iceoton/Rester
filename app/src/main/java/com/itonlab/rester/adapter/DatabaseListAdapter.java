package com.itonlab.rester.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.MenuItem;

import java.util.ArrayList;

public class DatabaseListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<MenuItem> menuItems;

    public DatabaseListAdapter(Context mContext, ArrayList<MenuItem> menuItems) {
        this.mContext = mContext;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menuItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.database_list_item,parent, false);
        }

        MenuItem menuItem = menuItems.get(position);
        TextView tvCode = (TextView) convertView.findViewById(R.id.tvCode);
        tvCode.setText(String.valueOf(menuItem.getCode()));
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(menuItem.getNameThai());
        TextView tvPrice  = (TextView) convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(String.valueOf(menuItem.getPrice()));

        return convertView;
    }
}
