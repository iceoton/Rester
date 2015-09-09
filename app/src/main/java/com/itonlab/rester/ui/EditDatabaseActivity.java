package com.itonlab.rester.ui;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itonlab.rester.R;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.FoodItem;
import com.itonlab.rester.model.MenuTable;

public class EditDatabaseActivity  extends Activity{
    private int menuId;
    private ResterDao databaseDao;
    private FoodItem foodItem;
    private EditText etName, etPrice;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_database);

        databaseDao = new ResterDao(EditDatabaseActivity.this);
        databaseDao.open();

        menuId = getIntent().getIntExtra(MenuTable.Columns._ID,0);
        Log.d("DATABASE", "menu_id = " + menuId);

        foodItem = databaseDao.getMenuAtId(menuId);
        etName = (EditText) findViewById(R.id.etName);
        etName.setText(foodItem.getNameThai());
        etPrice = (EditText) findViewById(R.id.etPrice);
        etPrice.setText(String.valueOf(foodItem.getPrice()));

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
                finish();
            }
        });
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

    private void saveToDatabase(){
        String menuName = etName.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        foodItem.setNameThai(menuName);
        foodItem.setPrice(price);
        databaseDao.updateMenu(foodItem);
    }
}
