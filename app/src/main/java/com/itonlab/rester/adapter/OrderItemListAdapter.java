package com.itonlab.rester.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.OrderItemDetail;

import java.util.ArrayList;

public class OrderItemListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<OrderItemDetail> orderItemDetails;
    int listItemLayout = R.layout.bill_list_item;

    public OrderItemListAdapter(Context mContext, ArrayList<OrderItemDetail> orderItemDetails) {
        this.mContext = mContext;
        this.orderItemDetails = orderItemDetails;
    }

    public OrderItemListAdapter(Context mContext, ArrayList<OrderItemDetail> orderItemDetails, int listItemLayout) {
        this.mContext = mContext;
        this.orderItemDetails = orderItemDetails;
        this.listItemLayout = listItemLayout;
    }

    @Override
    public int getCount() {
        return orderItemDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItemDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orderItemDetails.get(position).getPreOderId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(listItemLayout, parent, false);
        }

        OrderItemDetail orderItemDetail = orderItemDetails.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(orderItemDetail.getName());
        TextView tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(orderItemDetail.getQuantity() + "x");
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(orderItemDetail.getPrice()));
        TextView tvTotalPrice = (TextView)convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = orderItemDetail.getPrice() * orderItemDetail.getQuantity();
        tvTotalPrice.setText(Double.toString(totalPrice));
        if (listItemLayout == R.layout.summary_list_item) {
            TextView tvOption = (TextView) convertView.findViewById(R.id.textViewOption);
            tvOption.setText(orderItemDetail.getOption());
        }

        return convertView;
    }
}
