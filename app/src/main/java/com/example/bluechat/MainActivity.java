package com.example.bluechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageButton send,receive;
    BluetoothAdapter bt;
    ArrayList<String> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.send);
        bt = BluetoothAdapter.getDefaultAdapter();

        arrayList = new ArrayList<>();
        receive = findViewById(R.id.recieve);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bt == null)
                {
                    Toast.makeText(MainActivity.this, "THIS DEVICE DOES NOT SUPPORT BLUETOOTH", Toast.LENGTH_LONG).show();
                }else {
                    if(!bt.isEnabled()){
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        accesspermission();

                    }

                    Intent intent = new Intent(MainActivity.this,searchDevice.class);
                    bt.enable();
                    startActivity(intent);

                }

            }
        });


        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HostConnection.class);
               bt.enable();
                startActivity(intent);
            }
        });
    }

    public void accesspermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_ADMIN},3);
        }




    }
}