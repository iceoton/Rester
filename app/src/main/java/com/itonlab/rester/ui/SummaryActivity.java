package com.itonlab.rester.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.SummaryListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.model.SummaryItem;
import com.itonlab.rester.util.AppPreference;
import com.itonlab.rester.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class SummaryActivity extends Activity{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<SummaryItem> summaryItems;
    ResterDao databaseDao;

    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        server = new SimpleTCPServer(TCP_PORT);
        databaseDao = new ResterDao(SummaryActivity.this);
        databaseDao.open();

        summaryItems = databaseDao.getSummaryOrder();
        ListView lvSummmary = (ListView)findViewById(R.id.lvBillList);
        SummaryListAdapter summaryListAdapter = new SummaryListAdapter(SummaryActivity.this, summaryItems);
        lvSummmary.setAdapter(summaryListAdapter);
        TextView tvTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice()));

        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        if(summaryItems.size() > 0) {
            btnConfirm.setOnClickListener(confirmOnItemClickListener);
        } else {
            btnConfirm.setEnabled(false);
        }

    }

    @Override
    protected void onResume() {
        databaseDao.open();
        super.onResume();
        server.start();
    }

    @Override
    protected void onPause() {
        databaseDao.close();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();

    }

    private double findTotalPrice(){
        double totalPrice = 0;
        for(SummaryItem summaryItem : summaryItems){
            totalPrice += (summaryItem.getPrice() * summaryItem.getAmount());
        }

        return totalPrice;
    }

    View.OnClickListener confirmOnItemClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            sendOrderToMaster();
        }
    };

    private void sendOrderToMaster(){
        ArrayList<PreOrderItem> preOrderItems = databaseDao.getAllPreOrderItem();
        JsonFunction jsonFunction = new JsonFunction(SummaryActivity.this);
        String json = jsonFunction.getStringJSONOrder(preOrderItems);
        Log.d("JSON", json);

        AppPreference appPreference = new AppPreference(SummaryActivity.this);
        String ip = appPreference.getMasterIP();
        SimpleTCPClient.send(json, ip, TCP_PORT, new SimpleTCPClient.SendCallback() {
            public void onSuccess(String tag) {
                databaseDao.clearPreOrder();
                finish();
            }

            public void onFailed(String tag) {
                AlertDialog alertDialog = new AlertDialog.Builder(SummaryActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Can't connect to master");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }, "TAG");

    }

}
