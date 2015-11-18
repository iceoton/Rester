package com.itonlab.rester.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.OrderItemDetail;

import java.util.ArrayList;

public class OrderItemListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<OrderItemDetail> orderItemDetails;

    public OrderItemListAdapter(Context mContext, ArrayList<OrderItemDetail> orderItemDetails) {
        this.mContext = mContext;
        this.orderItemDetails = orderItemDetails;
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_item_list_item, parent, false);
        }

        OrderItemDetail orderItemDetail = orderItemDetails.get(position);

        LinearLayout layoutOrderItemListItem = (LinearLayout)
                convertView.findViewById(R.id.layoutOrderItemListItem);
        Drawable bgDrawable;
        if (orderItemDetail.isOrdered()) {
            bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_gray);
            convertView.setClickable(true);
        } else {
            bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_green);
            convertView.setClickable(false);
        }
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            layoutOrderItemListItem.setBackground(bgDrawable);
        } else {
            layoutOrderItemListItem.setBackgroundDrawable(bgDrawable);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(orderItemDetail.getName());
        TextView tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(orderItemDetail.getQuantity() + "รายการx");
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(orderItemDetail.getPrice()) + "บาท");
        TextView tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = orderItemDetail.getPrice() * orderItemDetail.getQuantity();
        tvTotalPrice.setText(Double.toString(totalPrice) + "บาท");

        TextView tvOption = (TextView) convertView.findViewById(R.id.textViewOption);
        tvOption.setText(orderItemDetail.getOption());


        return convertView;
    }
}
