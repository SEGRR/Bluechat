package com.example.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class HostConnection extends AppCompatActivity {


    BluetoothAdapter bt;
    BluetoothServerSocket mmServerSocket;
    BluetoothSocket socket;
    Thread connectionFinder;
    UUID uuid;
    public  static BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_connection);
        bt = BluetoothAdapter.getDefaultAdapter();

        if(!bt.isEnabled())
            bt.enable();

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(intent);

        uuid = UUID.fromString("a2932642-bd7e-11ec-9d64-0242ac120002");
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = this.bt.listenUsingRfcommWithServiceRecord(bt.getName(),uuid);
        } catch (IOException e) {
            Log.e("ERROR", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;


        connectionFinder =  new Thread(new Runnable() {
            @Override
            public void run() {
                socket = null;
                // Keep listening until exception occurs or a socket is returned.
                while (true) {
                    try {
                        socket = mmServerSocket.accept();
                    } catch (IOException e) {
                        Log.e("ERROR", "Socket's accept() method failed", e);
                        Toast.makeText(HostConnection.this, "Cannot Accept request", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (socket != null) {
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.

                                bluetoothSocket = socket;
                                Intent intent = new Intent(HostConnection.this,Chat.class);
                                intent.putExtra("connected_device","FROM");
                                startActivity(intent);

                        try {
                            mmServerSocket.close();
                        } catch (Exception e) {
                            Log.e("ERROR", "error while closing socket", e);
                        }
                        break;
                    }
                }
            }
        });
        connectionFinder.start();

    }
}