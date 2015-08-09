package com.itonlab.rester.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.itonlab.rester.R;

public class HomeFragment extends Fragment{
    Button btnViewOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container, false);
        btnViewOrder = (Button)rootView.findViewById(R.id.btnViewOrder);
        btnViewOrder.setOnClickListener(viewOrderOnClickListener);

        return rootView;
    }

    private View.OnClickListener viewOrderOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SummaryActivity.class);
            getActivity().startActivity(intent);
        }
    };
}
