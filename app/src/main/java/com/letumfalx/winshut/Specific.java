package com.letumfalx.winshut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.letumfalx.winshut.utils.Client;
import com.letumfalx.winshut.utils.ConnectionEvent;
import com.letumfalx.winshut.utils.ConnectionEventListener;
import com.letumfalx.winshut.utils.ConnectivityChangeAdapter;
import com.letumfalx.winshut.utils.ConnectivityChangeListener;
import com.letumfalx.winshut.utils.Wifi;

import java.util.regex.Pattern;

public class Specific extends AppCompatActivity {

    private EditText address;
    private Button connect;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific);

        connect = (Button)findViewById(R.id.specific_connect);
        back = (Button)findViewById(R.id.specific_back);
        address = (EditText)findViewById(R.id.specific_address);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                address.setEnabled(false);
                connect.setEnabled(false);
                connect.setText("Connecting...");

                String ip = address.getText().toString().trim();
                if(Pattern.matches("^\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}$", ip)) {
                    Client.start(ip);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(Specific.this)
                                    .setCancelable(false)
                                    .setMessage("Invalid IP Address pattern.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            address.setEnabled(true);
                                            connect.setEnabled(true);
                                            connect.setText("Connect");
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

        Client.addConnectionEventListener(connectionEventListener);
        Wifi.addConnectivityChangeListener(connectivityChangeListener);

    }


    private void onWifiDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Specific.this)
                        .setMessage("You have been disconnected to the local area network.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void onConnectionFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Specific.this)
                        .setCancelable(false)
                        .setMessage("Cannot connect to " + address.getText() + ".")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                address.setEnabled(true);
                                connect.setEnabled(true);
                                connect.setText("Connect");
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Client.removeConnectionEventListener(connectionEventListener);
        Wifi.removeConnectivityChangeListener(connectivityChangeListener);
        if(!Client.isConnected()) {
           Client.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        if(Wifi.isConnected()) {
            super.onResume();
        }
        else {
            onWifiDisconnect();
        }
    }

    private ConnectivityChangeListener connectivityChangeListener = new ConnectivityChangeAdapter()
    {
        @Override
        public void onDisconnect() {
            onWifiDisconnect();
        }
    };

    private ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
        @Override
        public void onConnecting(ConnectionEvent event) {
        }

        @Override
        public void onConnected(ConnectionEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(Specific.this)
                            .setCancelable(false)
                            .setMessage("You have successfully connected to "
                                    + address.getText() + ".")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Specific.this, Menu.class));
                                    finish();
                                }
                            }).show();
                }
            });
        }

        @Override
        public void onDisconnect(ConnectionEvent event) {
            onConnectionFailed();
        }

        @Override
        public void onStop(ConnectionEvent event) {
            onConnectionFailed();
        }

        @Override
        public void onError(String message) {
            onConnectionFailed();
        }
    };

}
