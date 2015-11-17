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
import com.itonlab.rester.util.JsonFunction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class HistoryFragment extends Fragment{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private ResterDao databaseDao;
    private ArrayList<Order> orders;
    private ListView listViewOrder;
    private OrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            @Override
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getActivity());
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
            }
        });

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
                String take = "*" + getActivity().getResources().getString(R.string.text_take_here);
                if (orders.get(position).getTake().equals(Order.Take.HOME)) {
                    take = "*" + getActivity().getResources().getString(R.string.text_take_home);
                }

                Intent intent = new Intent(getActivity(), HistoryDetailActivity.class);
                intent.putExtra(OrderTable.Columns._ID, orderId);
                intent.putExtra(OrderTable.Columns._ORDER_TIME, orderTime);
                intent.putExtra(OrderTable.Columns._TAKE, take);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        server.start();
        databaseDao.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseDao.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
    }
}
