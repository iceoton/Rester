package com.itonlab.rester.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.SummaryListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.SummaryItem;

import java.util.ArrayList;

public class SummaryActivity extends Activity{
    ArrayList<SummaryItem> summaryItems;
    ResterDao databaseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        databaseDao = new ResterDao(SummaryActivity.this);
        databaseDao.open();

        summaryItems = databaseDao.getSummaryOrder();
        ListView lvSummmary = (ListView)findViewById(R.id.lvOrderList);
        SummaryListAdapter summaryListAdapter = new SummaryListAdapter(SummaryActivity.this, summaryItems);
        lvSummmary.setAdapter(summaryListAdapter);
        TextView tvTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

    }

    @Override
    protected void onResume() {
        databaseDao.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        databaseDao.close();
        super.onPause();
    }

    private double findTotalPrice(){
        double totalPrice = 0;
        for(SummaryItem summaryItem : summaryItems){
            totalPrice += (summaryItem.getPrice() * summaryItem.getAmount());
        }

        return totalPrice;
    }
}
