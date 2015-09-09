package com.itonlab.rester.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.util.AppPreference;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.TCPUtils;

public class SettingsFragment extends Fragment{
    private TextView textViewIP;
    private EditText editTextIP, editTextName;
    private LinearLayout layoutEditDatabase;

    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings,container, false);

        textViewIP = (TextView) rootView.findViewById(R.id.textViewIP);
        textViewIP.setText(TCPUtils.getIP(getActivity()));

        editTextName = (EditText) rootView.findViewById(R.id.etYourName);

        editTextIP = (EditText) rootView.findViewById(R.id.editTextIP);
        TCPUtils.forceInputIP(editTextIP);

        layoutEditDatabase = (LinearLayout)rootView.findViewById(R.id.layoutEditDatabase);
        layoutEditDatabase.setOnClickListener(layoutEditDatabaseListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettingsValue();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettingsValue();

    }

    private void saveSettingsValue(){
        String yourName = editTextName.getText().toString().trim();
        String masterIP = editTextIP.getText().toString().trim();

        AppPreference appPreference = new AppPreference(getActivity());
        appPreference.saveYourName(yourName);
        appPreference.saveMasterIP(masterIP);
    }

    private void loadSettingsValue(){
        AppPreference appPreference = new AppPreference(getActivity());
        String yourName = appPreference.getYourName();
        editTextName.setText(yourName);
        String masterIP = appPreference.getMasterIP();
        editTextIP.setText(masterIP);
    }

    View.OnClickListener layoutEditDatabaseListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShowDatabaseActivity.class);
            getActivity().startActivity(intent);
        }
    };
}
