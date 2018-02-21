package com.letumfalx.winshut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.letumfalx.winshut.utils.Client;
import com.letumfalx.winshut.utils.Wifi;

public class Cancel extends AppCompatActivity {

    private ImageButton cancel;
    private Button back;
    private TextView sequenceText;
    private TextView timeText;

    private boolean stopClient = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);

        cancel = (ImageButton)findViewById(R.id.cancel_button);
        back = (Button)findViewById(R.id.cancel_back);
        sequenceText = (TextView) findViewById(R.id.cancel_sequence);
        timeText = (TextView) findViewById(R.id.cancel_time);


//        Wifi.addConnectivityChangeListener(connectivityChangeListener);
//        Client.addConnectionEventListener(connectionEventListener);
//        Client.addDataEventListener(dataEventListener);
    }

    private void onWifiDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Cancel.this)
                        .setMessage("You have been disconnected to the local area network.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                stopClient = true;
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void onClientDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Cancel.this)
                        .setMessage("You have been disconnected to the server.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                stopClient = true;
                                finish();
                            }
                        }).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Cancel.this)
                        .setCancelable(false)
                        .setMessage("Do you really want to disconnect?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
//        Wifi.removeConnectivityChangeListener(connectivityChangeListener);
//        Client.removeConnectionEventListener(connectionEventListener);
//        Client.removeDataEventListener(dataEventListener);
        super.onDestroy();
    }
}
