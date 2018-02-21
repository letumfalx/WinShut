package com.letumfalx.winshut;

import android.app.Activity;
import android.app.AlertDialog;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.appwidget.*;
import android.view.*;

import com.letumfalx.winshut.utils.ConnectivityChangeListener;
import com.letumfalx.winshut.utils.Data;
import com.letumfalx.winshut.utils.Wifi;

public class Main extends AppCompatActivity {


    private Button buttonWifi;
    private Button buttonSearch;
    private Button buttonSpecific;

    private ConnectivityChangeListener onConnectionChangeEvent = new ConnectivityChangeListener() {
        @Override
        public void onConnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonWifi.setEnabled(false);
                    buttonSearch.setEnabled(true);
                    buttonSpecific.setEnabled(true);
                }
            });

        }

        @Override
        public void onDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonWifi.setEnabled(true);
                    buttonSearch.setEnabled(false);
                    buttonSpecific.setEnabled(false);
                }
            });
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Wifi.setCurrentActivity(this);

        buttonWifi = (Button)findViewById(R.id.main_button_wifi);
        buttonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Wifi.isConnected()) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }
        });

        buttonSpecific = (Button)findViewById(R.id.main_button_specific);
        buttonSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Wifi.isConnected()) {
                    startActivity(new Intent(Main.this, Specific.class));
                }
            }
        });

        buttonSearch = (Button)findViewById(R.id.main_button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Wifi.isConnected()) {
                    startActivity(new Intent(Main.this, Search.class));
                    //startActivity(new Intent(Main.this, Menu.class));
                }
            }
        });

        Wifi.addConnectivityChangeListener(onConnectionChangeEvent);

    }

    @Override
    protected void onResume() {
        setButtonEnability();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Wifi.removeConnectivityChangeListener(onConnectionChangeEvent);
        super.onDestroy();
    }

    private void setButtonEnability() {
        boolean isConnected = Wifi.isConnected();
        buttonWifi.setEnabled(!isConnected);
        buttonSearch.setEnabled(isConnected);
        buttonSpecific.setEnabled(isConnected);
    }



}
