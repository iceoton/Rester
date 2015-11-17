package com.itonlab.rester.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.OrderItemListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.OrderItemDetail;
import com.itonlab.rester.model.OrderTable;

import java.util.ArrayList;

public class HistoryDetailActivity extends Activity {
    private int orderId;
    private String orderTime;
    private ResterDao databaseDao;
    private TextView tvOrderId, tvOrderTime, tvTake, tvTotalPrice;
    private ListView lvOrderItem;
    private OrderItemListAdapter orderItemListAdapter;
    private ArrayList<OrderItemDetail> orderItemDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        databaseDao = new ResterDao(HistoryDetailActivity.this);
        databaseDao.open();

        orderId = getIntent().getIntExtra(OrderTable.Columns._ID, 0);
        orderTime = getIntent().getStringExtra(OrderTable.Columns._ORDER_TIME);

        tvOrderId = (TextView) findViewById(R.id.tvOrderNumber);
        tvOrderId.setText(String.valueOf(orderId));
        tvOrderTime = (TextView) findViewById(R.id.tvOrderTime);
        tvOrderTime.setText(orderTime);

        orderItemDetails = databaseDao.getOrderDetail(orderId);
        orderItemListAdapter = new OrderItemListAdapter(HistoryDetailActivity.this, orderItemDetails);
        lvOrderItem = (ListView) findViewById(R.id.lvOrderItem);
        lvOrderItem.setAdapter(orderItemListAdapter);

        tvTake = (TextView) findViewById(R.id.textViewTake);
        tvTake.setText(getIntent().getStringExtra(OrderTable.Columns._TAKE));

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseDao.close();
    }

    private double findTotalPrice() {
        double totalPrice = 0;
        for (OrderItemDetail orderItemDetail : orderItemDetails) {
            totalPrice += (orderItemDetail.getPrice() * orderItemDetail.getQuantity());
        }

        return totalPrice;
    }
}
