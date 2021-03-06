package com.itonlab.rester.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.itonlab.rester.model.Picture;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.util.AppPreference;
import com.itonlab.rester.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class MenuFragment extends Fragment {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ListView lvFood;
    private ResterDao databaseDao;
    ArrayList<MenuItem> menuItems;

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
        server.start();
        databaseDao.open();
        super.onResume();
    }

    @Override
    public void onStop() {
        server.stop();
        databaseDao.close();
        super.onStop();
    }

    private AdapterView.OnItemClickListener menuOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialogForOrdering = new Dialog(getActivity());
            dialogForOrdering.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogForOrdering.setCancelable(true);
            dialogForOrdering.setContentView(R.layout.dialog_for_ordering);
            // show detail of food
            MenuItem menuItem = menuItems.get(position);

            TextView tvName = (TextView) dialogForOrdering.findViewById(R.id.tvName);
            AppPreference appPreference = new AppPreference(getActivity());
            if (appPreference.getAppLanguage().equals("th")) {
                tvName.setText(menuItem.getNameThai());
            } else {
                tvName.setText(menuItem.getNameEng());
            }

            TextView tvPrice = (TextView) dialogForOrdering.findViewById(R.id.tvPrice);
            tvPrice.setText(Double.toString(menuItem.getPrice()));
            ImageView ivImgFood = (ImageView) dialogForOrdering.findViewById(R.id.ivImgFood);

            Picture picture = databaseDao.getMenuPicture(menuItem.getPictureId());
            ivImgFood.setImageBitmap(picture.getBitmapPicture());
            // hidden keyboard by default
            dialogForOrdering.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            dialogForOrdering.show();

            Button btnCancel = (Button) dialogForOrdering.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogForOrdering.cancel();
                }
            });

            final PreOrderItem preOrderItem = new PreOrderItem();
            preOrderItem.setMenuCode(menuItem.getCode());
            final EditText etAmount = (EditText) dialogForOrdering.findViewById(R.id.etAmount);
            final EditText etOption = (EditText) dialogForOrdering.findViewById(R.id.editTextOption);
            Button btnOK = (Button) dialogForOrdering.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    String option = etOption.getText().toString();
                    preOrderItem.setQuantity(amount);
                    preOrderItem.setOption(option);
                    databaseDao.addPreOrderItem(preOrderItem);
                    dialogForOrdering.dismiss();
                }
            });
        }
    };



}
