package com.itonlab.rester;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itonlab.rester.adapter.FoodListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.FoodItem;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    ListView lvFood;
    private ResterDao databaseDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseDao = new ResterDao(getActivity());
        databaseDao.open();

        View rootView = inflater.inflate(R.layout.fragment_menu,container, false);
        lvFood = (ListView)rootView.findViewById(R.id.listFood);

        ArrayList<FoodItem> foodItems = databaseDao.getMenu();
        FoodListAdapter foodListAdapter = new FoodListAdapter(getActivity(),foodItems);
        lvFood.setAdapter(foodListAdapter);

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
}
