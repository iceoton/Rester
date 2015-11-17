package com.itonlab.rester.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.util.AppPreference;
import com.itonlab.rester.util.JsonFunction;

import java.util.Locale;

import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class SettingsFragment extends Fragment{
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    private TextView textViewIP;
    private EditText editTextIP, editTextName;
    private LinearLayout layoutEditDatabase;
    private Button btnThaiLanguage, btnEngLanguage;


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

        View rootView = inflater.inflate(R.layout.fragment_settings,container, false);

        textViewIP = (TextView) rootView.findViewById(R.id.textViewIP);
        textViewIP.setText(TCPUtils.getIP(getActivity()));

        editTextName = (EditText) rootView.findViewById(R.id.etYourName);

        editTextIP = (EditText) rootView.findViewById(R.id.editTextIP);
        TCPUtils.forceInputIP(editTextIP);

        layoutEditDatabase = (LinearLayout)rootView.findViewById(R.id.layoutEditDatabase);
        layoutEditDatabase.setOnClickListener(layoutEditDatabaseListener);

        btnThaiLanguage = (Button) rootView.findViewById(R.id.btnThaiLanguage);
        btnThaiLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration config = new Configuration();
                config.locale = new Locale("th");
                getResources().updateConfiguration(config, null);
                AppPreference appPreference = new AppPreference(getActivity());
                appPreference.saveAppLanguage("th");

                getActivity().recreate();
            }
        });

        btnEngLanguage = (Button) rootView.findViewById(R.id.btnEngLanguage);
        btnEngLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration config = new Configuration();
                config.locale = Locale.ENGLISH;
                getResources().updateConfiguration(config, null);
                AppPreference appPreference = new AppPreference(getActivity());
                appPreference.saveAppLanguage("en");

                getActivity().recreate();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        server.start();
        loadSettingsValue();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
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
            showDialogPassword();
        }
    };

    private void showDialogPassword(){

        final Dialog dialogLogin = new Dialog(getActivity());
        dialogLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLogin.setContentView(R.layout.dialog_login);
        dialogLogin.setCancelable(true);
        dialogLogin.show();

        final EditText etPassword  = (EditText) dialogLogin.findViewById(R.id.etPassword);
        Button btnLogin = (Button) dialogLogin.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference appPreference = new AppPreference(getActivity());
                String savePassword = appPreference.getAppPassword();
                String inputPassword = etPassword.getText().toString().trim();
                if (inputPassword.equals(savePassword)) {
                    //Logged in
                    Intent intent = new Intent(getActivity(), ShowDatabaseActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });


    }
}
