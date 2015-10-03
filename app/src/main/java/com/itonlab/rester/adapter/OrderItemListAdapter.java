package com.itonlab.rester.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.OrderDetailItem;

import java.util.ArrayList;

public class OrderItemListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<OrderDetailItem> orderDetailItems;
    int listItemLayout = R.layout.bill_list_item;

    public OrderItemListAdapter(Context mContext, ArrayList<OrderDetailItem> orderDetailItems) {
        this.mContext = mContext;
        this.orderDetailItems = orderDetailItems;
    }

    public OrderItemListAdapter(Context mContext, ArrayList<OrderDetailItem> orderDetailItems, int listItemLayout) {
        this.mContext = mContext;
        this.orderDetailItems = orderDetailItems;
        this.listItemLayout = listItemLayout;
    }

    @Override
    public int getCount() {
        return orderDetailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDetailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orderDetailItems.get(position).getPreOderId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(listItemLayout, parent, false);
        }

        OrderDetailItem orderDetailItem = orderDetailItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(orderDetailItem.getName());
        TextView tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(orderDetailItem.getAmount()+ "x");
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(orderDetailItem.getPrice()));
        TextView tvTotalPrice = (TextView)convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = orderDetailItem.getPrice() * orderDetailItem.getAmount();
        tvTotalPrice.setText(Double.toString(totalPrice));

        return convertView;
    }
}
