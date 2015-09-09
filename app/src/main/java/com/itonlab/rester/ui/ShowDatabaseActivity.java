package com.itonlab.rester.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.DatabaseListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.FoodItem;
import com.itonlab.rester.model.MenuTable;

import java.util.ArrayList;

public class ShowDatabaseActivity extends Activity {
    private ResterDao databaseDao;
    private ListView lvData;
    private ArrayList<FoodItem> foodItems;
    DatabaseListAdapter databaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_database);

        databaseDao = new ResterDao(ShowDatabaseActivity.this);
        databaseDao.open();

        lvData = (ListView) findViewById(R.id.listData);
        foodItems = databaseDao.getMenu();
        lvData.setOnItemClickListener(listDataOnItemClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseDao.open();
        foodItems = databaseDao.getMenu();
        databaseListAdapter = new DatabaseListAdapter(ShowDatabaseActivity.this, foodItems);
        lvData.setAdapter(databaseListAdapter);
    }

    @Override
    protected void onPause() {
        databaseDao.close();
        super.onPause();
    }

    AdapterView.OnItemClickListener listDataOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int itemId = (int)id;
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowDatabaseActivity.this);
            builder.setMessage("ต้องการแก้ไขข้อมูลใช่หรือไม่?")
                    .setPositiveButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("แก้ไข", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ShowDatabaseActivity.this, EditDatabaseActivity.class);
                            intent.putExtra(MenuTable.Columns._ID, itemId);
                            startActivity(intent);
                        }
                    });
            // Create the AlertDialog object
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

}
