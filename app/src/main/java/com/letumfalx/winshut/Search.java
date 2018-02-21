package com.letumfalx.winshut;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.letumfalx.winshut.utils.*;
import com.letumfalx.winshut.utils.ConnectionEvent;
import com.letumfalx.winshut.utils.ConnectionEventListener;

import java.util.*;

import javax.sql.*;


public class Search extends AppCompatActivity {

    private SearchButton[] buttons = new SearchButton[5];
    private Button refresh;
    private Button back;
    private TextView title;
    private int titleLength;

    private Timer animation = new Timer();

    private Map<String, String> hosts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttons[0] = new SearchButton((Button)findViewById(R.id.search_button_1));
        buttons[1] = new SearchButton((Button)findViewById(R.id.search_button_2));
        buttons[2] = new SearchButton((Button)findViewById(R.id.search_button_3));
        buttons[3] = new SearchButton((Button)findViewById(R.id.search_button_4));
        buttons[4] = new SearchButton((Button)findViewById(R.id.search_button_5));

        for(SearchButton b : buttons) {
            b.button.setOnClickListener(ocl);
        }

        back = (Button)findViewById(R.id.search_button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        refresh = (Button)findViewById(R.id.search_button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hosts.clear();
                for(SearchButton button : buttons) {
                    button.hostname = "";
                    button.ipAddress = "";
                    button.setVisibility(View.INVISIBLE);
                }
            }
        });

        title = (TextView)findViewById(R.id.search_label_title);
        titleLength = title.getText().length();
        animation.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(title.getText() + ".");
                        if(title.getText().length() > titleLength + 3) {
                            title.setText(title.getText().toString().substring(0, titleLength));
                        }
                    }
                });
            }
        }, 150, 750);


        Wifi.addConnectivityChangeListener(wifiConnectivityChangeListener);
        Broadcast.addDataEventListener(broadcastDataEventListener);
        Client.addConnectionEventListener(clientConnectionEventListener);
        Broadcast.start();
    }

    private ConnectivityChangeListener wifiConnectivityChangeListener = new ConnectivityChangeAdapter() {
        @Override
        public void onDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDisconnectMessage();
                }
            });

        }
    };

    private DataEventListener broadcastDataEventListener = new DataEventAdapter() {
        @Override
        public void onReceive(DataEvent event) {
            if(hosts.size() >= 5 || !event.getData().getKey().equalsIgnoreCase("hostname")) {
                return;
            }
            String ip = event.getConnectionDetails().getRemoteIPAddress().toLowerCase().trim();

            if(!hosts.containsKey(ip)) {
                hosts.put(ip, event.getData().get());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        Iterator<String> itr = hosts.keySet().iterator();
                        while(itr.hasNext()) {
                            String tmp = itr.next();
                            if(!buttons[i].ipAddress.equalsIgnoreCase(tmp)
                                    || !buttons[i].hostname.equals(hosts.get(tmp))) {

                                buttons[i].ipAddress = tmp;
                                buttons[i].hostname = hosts.get(tmp);
                                buttons[i].setVisibility(View.VISIBLE);
                                buttons[i].setText();
                            }
                            i++;
                        }

                        for(;i<buttons.length; ++i) {
                            buttons[i].ipAddress = "";
                            buttons[i].hostname = "";
                            buttons[i].setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }
    };

    private View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh.setEnabled(false);

                    int index = -1;
                    for(int i = 0; i < buttons.length; ++i) {
                        buttons[i].setEnabled(false);
                        if(view.equals(buttons[i].button)) {
                            index = i;
                            Button b = (Button)view;
                            b.setText("Connecting to " + b.getText());
                        }
                    }
                    Client.start(buttons[index].ipAddress);
                }
            });
        }
    };

    private ConnectionEventListener clientConnectionEventListener = new ConnectionEventAdapter() {
        @Override
        public void onConnecting(ConnectionEvent event) {

        }

        @Override
        public void onConnected(final ConnectionEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                new AlertDialog.Builder(Search.this)
                        .setMessage("")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Search.this, Menu.class));
                                finish();
                            }
                        }).show();
                }
            });
        }

        @Override
        public void onDisconnect(final ConnectionEvent event) {
            final String message = event.getRemoteInetAddress() != null ?
                    "Has been disconnected to " + event.getRemoteHostname() + "." :
                    "Unable to create a connection.";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(Search.this)
                            .setCancelable(false)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for(SearchButton b: buttons) {
                                        b.setEnabled(true);
                                        b.setText();
                                    }
                                    refresh.setEnabled(true);
                                }
                            }).show();

                }
            });
        }

        @Override
        public void onStop(final ConnectionEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(Search.this)
                            .setCancelable(false)
                            .setMessage("Connection has been stopped.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for(SearchButton b: buttons) {
                                        b.setEnabled(true);
                                        b.setText();
                                    }
                                    refresh.setEnabled(true);
                                }
                            }).show();

                }
            });
        }

        @Override
        public void onError(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(Search.this)
                            .setCancelable(false)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for(SearchButton b: buttons) {
                                        b.setEnabled(true);
                                        b.setText();
                                    }
                                    refresh.setEnabled(true);
                                }
                            }).show();
                }
            });
        }
    };



    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Broadcast.stop();
        Wifi.removeConnectivityChangeListener(wifiConnectivityChangeListener);
        Broadcast.removeDataEventListener(broadcastDataEventListener);
        Broadcast.removeConnectionEventListener(clientConnectionEventListener);
        animation.cancel();
        if(!Client.isConnected()) {
            Client.stop();
        }
        Broadcast.stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(Wifi.isConnected()) {
            super.onResume();
        }
        else {
            showDisconnectMessage();
        }

    }

    private void showDisconnectMessage() {
        new AlertDialog.Builder(Search.this)
                .setMessage("You have been disconnected to the WiFi.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false).show();
    }

    private class SearchButton {

        private final Button button;
        private String ipAddress = "";
        private String hostname = "";


        private SearchButton(Button button) {
            this.button = button;
            this.button.setEnabled(true);
            this.button.setVisibility(View.INVISIBLE);
        }

        private void setEnabled(boolean enabled) {
            this.button.setEnabled(enabled);
        }

        private void setVisibility(int visibility) {
            this.button.setVisibility(visibility);
        }

        private void setText() {
            String text = hostname.length() <= ipAddress.length() ? hostname : ipAddress;
            button.setText(text);
        }

        private String getText() {
            return button.getText().toString();
        }

        private void setText(String text) {
            button.setText(text);
        }
    }
}
