package com.itonlab.rester.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.model.FoodItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<FoodItem>  foodItems;

    public FoodListAdapter(Context context,ArrayList<FoodItem> foodItems){
        this.mContext = context;
        this.foodItems = foodItems;
    }

    @Override
    public int getCount() {
        return foodItems.size();
    }

    @Override
    public Object getItem(int position) {
        return foodItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return foodItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.food_list_item, parent, false);
        }
        FoodItem foodItem = foodItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(foodItem.getNameThai());
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(foodItem.getPrice()));
        ImageView ivImgFood = (ImageView)convertView.findViewById(R.id.ivImgFood);
        Drawable drawable = getDrawableFromAsset(foodItem.getImgPath());
        ivImgFood.setImageDrawable(drawable);

        return convertView;
    }

    private Drawable getDrawableFromAsset(String assetPath)
    {
        AssetManager assetManager = mContext.getAssets();
        // To load image
        InputStream inputStream = null;
        try {
            // get input stream
            inputStream = assetManager.open(assetPath);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        // create drawable from stream
        Drawable drawable = Drawable.createFromStream(inputStream, null);

        return drawable;
    }
}
