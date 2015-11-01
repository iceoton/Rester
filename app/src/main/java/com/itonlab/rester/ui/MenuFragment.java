package com.itonlab.rester.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
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
import com.itonlab.rester.adapter.MenuListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.MenuItem;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.util.FileManager;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    ListView lvFood;
    private ResterDao databaseDao;
    ArrayList<MenuItem> menuItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseDao = new ResterDao(getActivity());
        databaseDao.open();

        View rootView = inflater.inflate(R.layout.fragment_menu,container, false);
        lvFood = (ListView)rootView.findViewById(R.id.listFood);

        menuItems = databaseDao.getMenu();
        MenuListAdapter menuListAdapter = new MenuListAdapter(getActivity(), menuItems);
        lvFood.setAdapter(menuListAdapter);
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
            final Dialog dialogForOrdering = new Dialog(getActivity());
            dialogForOrdering.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogForOrdering.setCancelable(true);
            dialogForOrdering.setContentView(R.layout.dialog_for_ordering);
            // show detail of food
            MenuItem menuItem = menuItems.get(position);
            TextView tvName = (TextView) dialogForOrdering.findViewById(R.id.tvName);
            tvName.setText(menuItem.getNameThai());
            TextView tvPrice = (TextView) dialogForOrdering.findViewById(R.id.tvPrice);
            tvPrice.setText(Double.toString(menuItem.getPrice()));
            ImageView ivImgFood = (ImageView) dialogForOrdering.findViewById(R.id.ivImgFood);
            FileManager fileManager = new FileManager(getActivity());
            Drawable drawable = fileManager.getDrawableFromAsset(menuItem.getImgPath());
            ivImgFood.setImageDrawable(drawable);
            dialogForOrdering.show();

            Button btnCancel = (Button) dialogForOrdering.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogForOrdering.cancel();
                }
            });

            final PreOrderItem preOrderItem = new PreOrderItem();
            preOrderItem.setMenuId(menuItem.getId());
            final EditText etAmount = (EditText) dialogForOrdering.findViewById(R.id.etAmount);
            Button btnOK = (Button) dialogForOrdering.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    preOrderItem.setAmount(amount);
                    databaseDao.addPreOrderItem(preOrderItem);
                    dialogForOrdering.dismiss();
                }
            });
        }
    };



}
