package com.itonlab.rester.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.OrderListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.Order;
import com.itonlab.rester.model.OrderTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryFragment extends Fragment{
    private ResterDao databaseDao;
    private ArrayList<Order> orders;
    private ListView listViewOrder;
    private OrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history,container, false);

        databaseDao = new ResterDao(getActivity());
        databaseDao.open();

        orders = databaseDao.getAllOrder();
        listViewOrder = (ListView) rootView.findViewById(R.id.listViewOrder);
        orderListAdapter = new OrderListAdapter(getActivity(), orders);
        listViewOrder.setAdapter(orderListAdapter);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // click to see order detail.
                int orderId = (int)id;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss", Locale.getDefault());
                String orderTime = dateFormat.format(orders.get(position).getOrderTime());
                Intent intent = new Intent(getActivity(), HistoryDetailActivity.class);
                intent.putExtra(OrderTable.Columns._ID, orderId);
                intent.putExtra(OrderTable.Columns._ORDER_TIME, orderTime);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseDao.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseDao.close();
    }
}
