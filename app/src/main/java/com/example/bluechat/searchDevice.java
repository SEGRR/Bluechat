package com.example.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class searchDevice extends AppCompatActivity {

    BluetoothAdapter bt;
    ArrayList<BluetoothDevice> availableDevices;
    ArrayAdapter<String> device_list;
    ArrayList<String> arrayList;
    BroadcastReceiver receiver;
    ListView lv;
    BluetoothSocket mmSocket;
    UUID uuid;
    String connected_device;
    Handler handler;
    public static BluetoothSocket bluetoothSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        uuid = UUID.fromString("a2932642-bd7e-11ec-9d64-0242ac120002");

        availableDevices = new ArrayList<>();

        arrayList = new ArrayList<>();
        lv = findViewById(R.id.devices_list);
        bt = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler();
        Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();

        if(!bt.isEnabled())
            bt.enable();


        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                availableDevices.add(device);
                arrayList.add(device.getName());
            }
        }
        device_list = new ArrayAdapter<String>(searchDevice.this,android.R.layout.simple_list_item_1,arrayList);
        lv.setAdapter(device_list);

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    availableDevices.add(device);
                    arrayList.add(device.getName());
                    //Log.i("Bluetooth Devices : ",device.getName());
                    device_list.notifyDataSetChanged();
                }


            }
        };





        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(searchDevice.this, ""+i, Toast.LENGTH_SHORT).show();

                BluetoothDevice selectedDevice =  availableDevices.get(i);
                connected_device = selectedDevice.getName();
                Toast.makeText(searchDevice.this, "Connecting To "+selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                BluetoothSocket tmp = null;


                try {
                    // Get a BluetoothSocket to connect with the given BluetoothDevice.
                    // MY_UUID is the app's UUID string, also used in the server code.
                    tmp = selectedDevice.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    Log.e("ERROR", "Socket's create() method failed", e);
                }

                mmSocket = tmp;




                Thread  connectToDevice = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // bt.cancelDiscovery();
                        try {

                            mmSocket.connect();

                            if(mmSocket.isConnected()){
                                manageMyConnectedSocket(mmSocket);
                            }
                        } catch (IOException connectException) {
                            try {
                                mmSocket.close();
                            } catch (IOException closeException) {
                                Log.e("ERROR", "Could not close the client socket", closeException);
                                Toast.makeText(searchDevice.this, "Cannot connect to This device", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                connectToDevice.start();
            }
        });


    }


    public  void manageMyConnectedSocket(BluetoothSocket mmSocket){

        bluetoothSocket = mmSocket;
        Intent intent = new Intent(getApplicationContext(),Chat.class);
        intent.putExtra("connected_device",connected_device);
        startActivity(intent);
    }



    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
