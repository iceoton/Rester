package com.itonlab.rester.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.OrderItemDetail;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.util.AppPreference;

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

        FrameLayout layoutOrderItemListItem = (FrameLayout)
                convertView.findViewById(R.id.layoutOrderItemListItem);
        TextView txtOrderItemStatus = (TextView) convertView.findViewById(R.id.txtItemStatus);

        if (orderItemDetail.isOrdered()) {
            convertView.setEnabled(false);
        }

        if (orderItemDetail.getStatus().equals(PreOrderItem.Status.DONE)) {
            Drawable bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_yellow);
            String strStatus = mContext.getResources().getString(R.string.order_item_status_done);
            int statusColor = mContext.getResources().getColor(R.color.yellow_shadow);

            if (orderItemDetail.isServed()) {
                bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_red);
                strStatus = mContext.getResources().getString(R.string.order_item_status_served);
                statusColor = mContext.getResources().getColor(R.color.red);
            }

            txtOrderItemStatus.setVisibility(View.VISIBLE);
            txtOrderItemStatus.setText(strStatus);
            txtOrderItemStatus.setTextColor(statusColor);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                layoutOrderItemListItem.setBackground(bgDrawable);
            } else {
                layoutOrderItemListItem.setBackgroundDrawable(bgDrawable);
            }
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        TextView tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = orderItemDetail.getPrice() * orderItemDetail.getQuantity();

        AppPreference appPreference = new AppPreference(mContext);
        String menuName;
        if (appPreference.getAppLanguage().equals("th")) {
            menuName = orderItemDetail.getNameTH();
        } else {
            menuName = orderItemDetail.getNameEN();
        }
        tvName.setText(menuName);
        tvNumber.setText(orderItemDetail.getQuantity() + mContext.getResources().getString(R.string.text_item) + "x");
        tvPrice.setText(Double.toString(orderItemDetail.getPrice()) + mContext.getResources().getString(R.string.text_baht));
        tvTotalPrice.setText(Double.toString(totalPrice) + mContext.getResources().getString(R.string.text_baht));

        TextView tvOption = (TextView) convertView.findViewById(R.id.textViewOption);
        tvOption.setText(orderItemDetail.getOption());


        return convertView;
    }
}
