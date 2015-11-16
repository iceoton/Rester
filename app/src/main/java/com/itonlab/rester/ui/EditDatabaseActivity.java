package com.itonlab.rester.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itonlab.rester.R;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.MenuItem;
import com.itonlab.rester.model.MenuTable;
import com.itonlab.rester.model.Picture;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditDatabaseActivity  extends Activity{
    private static final int PICTURE_REQUEST_CODE = 1;
    private int menuId;
    private ResterDao databaseDao;
    private MenuItem menuItem;
    private ImageView imageViewFood;
    private EditText etCode, etName, etPrice;
    private Button btnSave;
    private Picture pictureFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_database);

        databaseDao = new ResterDao(EditDatabaseActivity.this);
        databaseDao.open();

        imageViewFood = (ImageView) findViewById(R.id.ivImgFood);
        etCode = (EditText) findViewById(R.id.etCode);
        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        btnSave = (Button) findViewById(R.id.btnSave);

        menuId = getIntent().getIntExtra(MenuTable.Columns._ID,0);
        Log.d("DATABASE", "menu_id = " + menuId);
        if (menuId != 0) {
            // to edit data
            menuItem = databaseDao.getMenuAtId(menuId);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveToDatabase();
                }
            });

            etCode.setText(menuItem.getCode());
            etName.setText(menuItem.getNameThai());
            etPrice.setText(String.valueOf(menuItem.getPrice()));
            pictureFood = databaseDao.getMenuPicture(menuItem.getPictureId());
            imageViewFood.setImageBitmap(pictureFood.getBitmapPicture());
        } else {
            // to add new data
            menuItem = new MenuItem();
            btnSave.setText(getResources().getText(R.string.text_add));
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDataToDatabase();
                }
            });
        }

        imageViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,
                        "Select app to pick picture"), PICTURE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICTURE_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            Uri imageUri = data.getData();
            Bitmap imageBitmap;
            InputStream imageInputStream;
            try {
                imageInputStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(imageInputStream);
                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 300, 225, true);
                imageViewFood.setImageBitmap(resized);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
        menuItem.setNameThai(menuName);
        menuItem.setPrice(price);
        updateMenuPicture();
        databaseDao.updateMenu(menuItem);
        finish();
    }

    private void addDataToDatabase() {
        String menuName = etName.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        if (!menuName.equals("")) {
            menuItem.setNameThai(menuName);
            menuItem.setPrice(price);
            menuItem.setPictureId(addMenuPicture());
            databaseDao.addMenu(menuItem);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter menu name...", Toast.LENGTH_SHORT).show();
        }
    }

    private int addMenuPicture() {
        Drawable drawable = imageViewFood.getDrawable();
        pictureFood = new Picture();
        pictureFood.setPicture(drawable);
        return databaseDao.addMenuPicture(pictureFood);
    }

    private void updateMenuPicture() {
        Drawable drawable = imageViewFood.getDrawable();
        pictureFood = new Picture();
        pictureFood.setId(menuItem.getPictureId());
        pictureFood.setPicture(drawable);
        databaseDao.updateMenuPicture(pictureFood);
    }
}
