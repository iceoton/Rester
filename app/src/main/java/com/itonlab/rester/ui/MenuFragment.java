package com.itonlab.rester.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.FoodListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.FoodItem;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.util.FileManager;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    ListView lvFood;
    private ResterDao databaseDao;
    ArrayList<FoodItem> foodItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseDao = new ResterDao(getActivity());
        databaseDao.open();

        View rootView = inflater.inflate(R.layout.fragment_menu,container, false);
        lvFood = (ListView)rootView.findViewById(R.id.listFood);

        foodItems = databaseDao.getMenu();
        FoodListAdapter foodListAdapter = new FoodListAdapter(getActivity(),foodItems);
        lvFood.setAdapter(foodListAdapter);
        lvFood.setOnItemClickListener(menuOnItemClickListener);

        return rootView;
    }

    @Override
    public void onResume() {
        databaseDao.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        databaseDao.close();
        super.onPause();
    }

    private AdapterView.OnItemClickListener menuOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialogFoodDetail = new Dialog(getActivity());
            dialogFoodDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogFoodDetail.setCancelable(true);
            dialogFoodDetail.setContentView(R.layout.dialog_food_detail);
            // show detail of food
            FoodItem foodItem = foodItems.get(position);
            TextView tvName = (TextView)dialogFoodDetail.findViewById(R.id.tvName);
            tvName.setText(foodItem.getNameThai());
            TextView tvPrice = (TextView)dialogFoodDetail.findViewById(R.id.tvPrice);
            tvPrice.setText(Double.toString(foodItem.getPrice()));
            ImageView ivImgFood = (ImageView)dialogFoodDetail.findViewById(R.id.ivImgFood);
            FileManager fileManager = new FileManager(getActivity());
            Drawable drawable = fileManager.getDrawableFromAsset(foodItem.getImgPath());
            ivImgFood.setImageDrawable(drawable);
            dialogFoodDetail.show();

            Button btnCancel = (Button)dialogFoodDetail.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogFoodDetail.cancel();
                }
            });

            final PreOrderItem preOrderItem = new PreOrderItem();
            preOrderItem.setMenuId(foodItem.getId());
            final EditText etAmount = (EditText)dialogFoodDetail.findViewById(R.id.etAmount);
            Button btnOK= (Button)dialogFoodDetail.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    preOrderItem.setAmount(amount);
                    databaseDao.addToPreOrder(preOrderItem);
                    dialogFoodDetail.dismiss();
                }
            });
        }
    };



}
