package com.letumfalx.winshut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.letumfalx.winshut.utils.*;


public class Menu extends AppCompatActivity {

    private MenuButton[] sequenceButtons = new MenuButton[6];
    private MenuButton[] typeButtons = new MenuButton[3];
    private Button execute;
    private Button back;
    private TextView label;
    private boolean stopClient = false;
    private boolean showSequenceAlert = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sequenceButtons[0] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_1), 1);
        sequenceButtons[1] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_2), 2);
        sequenceButtons[2] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_3), 3);
        sequenceButtons[3] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_4), 4);
        sequenceButtons[4] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_5), 5);
        sequenceButtons[5] = new MenuButton((ImageButton)findViewById(R.id.menu_sequence_6), 6);

        typeButtons[0] = new MenuButton((ImageButton)findViewById(R.id.menu_type_1), 1);
        typeButtons[1] = new MenuButton((ImageButton)findViewById(R.id.menu_type_2), 2);
        typeButtons[2] = new MenuButton((ImageButton)findViewById(R.id.menu_type_3), 3);

        for(MenuButton mb : sequenceButtons) {
            mb.button.setOnClickListener(sequenceClick);
        }

        for(MenuButton mb : typeButtons) {
            mb.button.setOnClickListener(typeClick);
        }

        execute = (Button)findViewById(R.id.menu_execute);
        back = (Button)findViewById(R.id.menu_back);
        label = (TextView)findViewById(R.id.menu_sequence);
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                execute.setText("Executing...");
                execute.setEnabled(false);

                int _seq = 0;
                for(MenuButton mb : sequenceButtons) {
                    if(mb.isSelected()) {
                        _seq = mb.value;
                        break;
                    }
                }

                int _type = 0;
                for(MenuButton mb : typeButtons) {
                    if(mb.isSelected()) {
                        _type = mb.value;
                        break;
                    }
                }

                if(_seq <= 0) {
                    new AlertDialog.Builder(Menu.this)
                            .setCancelable(false)
                            .setMessage("Please select a sequence.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    execute.setText("Execute");
                                    execute.setEnabled(true);
                                }
                            }).show();
                    return;
                }

                if(_type <= 0) {
                    new AlertDialog.Builder(Menu.this)
                            .setCancelable(false)
                            .setMessage("Please select a type.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    execute.setText("Execute");
                                    execute.setEnabled(true);
                                }
                            }).show();
                    return;
                }

                Client.getStream().send("sequence", Integer.toString(_seq), Integer.toString(_type));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Wifi.addConnectivityChangeListener(connectivityChangeListener);
        Client.addConnectionEventListener(connectionEventListener);
        Client.addDataEventListener(dataEventListener);
        Sequence.addSequenceChangeEventListener(sequenceChangeEventListener);
    }

    private void onWifiDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Menu.this)
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
                new AlertDialog.Builder(Menu.this)
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

    private void onSequenceChangeEvent(final SequenceChangeEvent event) {
        if(event.sequence.value > SequenceList.NoShutdown.value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(showSequenceAlert) {
                        showSequenceAlert = false;
                        new AlertDialog.Builder(Menu.this)
                                .setCancelable(false)
                                .setMessage("Sequence has been change to " + event.type.text
                                        + " " + event.sequence.text + ".")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(Menu.this, Cancel.class));
                                        finish();
                                    }
                                }).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Wifi.removeConnectivityChangeListener(connectivityChangeListener);
        Client.removeConnectionEventListener(connectionEventListener);
        Client.removeDataEventListener(dataEventListener);
        Sequence.removeSequenceChangeEventListener(sequenceChangeEventListener);
        if(stopClient) Client.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Menu.this)
                        .setCancelable(false)
                        .setMessage("Do you really want to disconnect?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                stopClient = true;
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
    protected void onResume() {
        if(Wifi.isConnected()) {
            if(Client.isRunning() && Client.isConnected()) {
                super.onResume();
                if(Sequence.getCurrentSequence() != SequenceList.NoShutdown) {
                    if(Sequence.getCurrentSequence().value > SequenceList.NoShutdown.value) {
                        startActivity(new Intent(Menu.this, Cancel.class));
                        finish();
                    }
                }
            }
            else {
                onClientDisconnect();
            }
        }
        else {
            onWifiDisconnect();
        }
    }

    private View.OnClickListener sequenceClick = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int _sequence = 0;
                    for(MenuButton mb : sequenceButtons) {
                        if(view.equals(mb.button)) {
                            mb.setSelected(true);
                            _sequence = mb.value;
                        }
                        else {
                            mb.setSelected(false);
                        }
                    }

                    int _type = 0;
                    for(MenuButton mb : typeButtons) {
                        if (mb.isSelected()) {
                            _type = mb.value;
                            break;
                        }
                    }

                    label.setText(Sequence.getType(_type).text);
                    if(_sequence > 0) {
                        label.setText(label.getText() + " " + Sequence.getSequence(_sequence).text);
                    }
                }
            });
        }
    };

    private View.OnClickListener typeClick = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int _sequence = 0;
                    for(MenuButton mb : sequenceButtons) {
                        if (mb.isSelected()) {
                            _sequence = mb.value;
                            break;
                        }
                    }

                    int _type = 0;
                    for(MenuButton mb : typeButtons) {
                        if(view.equals(mb.button)) {
                            mb.setSelected(true);
                            _type = mb.value;
                        }
                        else {
                            mb.setSelected(false);
                        }
                    }

                    label.setText(Sequence.getType(_type).text);
                    if(_sequence > 0) {
                        label.setText(label.getText() + " " + Sequence.getSequence(_sequence).text);
                    }
                }
            });
        }
    };

    private ConnectivityChangeListener connectivityChangeListener = new ConnectivityChangeListener()
    {
        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {
            onWifiDisconnect();
        }
    };

    private ConnectionEventListener connectionEventListener = new ConnectionEventAdapter() {

        @Override
        public void onDisconnect(ConnectionEvent event) {
            onClientDisconnect();
        }

        @Override
        public void onStop(ConnectionEvent event) {
            onClientDisconnect();
        }

        @Override
        public void onError(String message) {
            onClientDisconnect();
        }
    };

    private DataEventListener dataEventListener = new DataEventAdapter() {
        @Override
        public void onReceive(DataEvent event) {
            if(event.getData().getKey().trim().equalsIgnoreCase("sequence")) {
                Sequence.setSequence(event.getData());
            }
        }
    };

    private SequenceChangeEventListener sequenceChangeEventListener =
            new SequenceChangeEventListener() {
        @Override
        public void onSequenceChange(SequenceChangeEvent event) {
            onSequenceChangeEvent(event);
        }

        @Override
        public void onError(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(Menu.this)
                            .setCancelable(false)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    execute.setText("Execute");
                                    execute.setEnabled(true);
                                }
                            }).show();
                }
            });
        }
    };

    private class MenuButton {

        private final int value;
        private final ImageButton button;

        private MenuButton(ImageButton button, int value) {
            this.value = value;
            this.button = button;
        }

        private void setSelected(boolean selected) {
            button.setEnabled(!selected);
        }

        private boolean isSelected() {
            return !button.isEnabled();
        }

    }


}
