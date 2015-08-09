package com.itonlab.rester.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.SummaryItem;

import java.util.ArrayList;

public class SummaryListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<SummaryItem> summaryItems;

    public SummaryListAdapter(Context mContext, ArrayList<SummaryItem> summaryItems) {
        this.mContext = mContext;
        this.summaryItems = summaryItems;
    }

    @Override
    public int getCount() {
        return summaryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return summaryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
        }

        SummaryItem summaryItem = summaryItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(summaryItem.getName());
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(summaryItem.getPrice()));
        TextView tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(summaryItem.getAmount()+ "x");
        TextView tvTotalPrice = (TextView)convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = summaryItem.getPrice() * summaryItem.getAmount();
        tvTotalPrice.setText(Double.toString(totalPrice));

        return convertView;
    }
}
