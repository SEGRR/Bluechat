package com.example.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Chat extends AppCompatActivity {

    Button send,closeConnection;
    TextView msgbox;
    EditText msginput;
    DataInputStream input;
    DataOutputStream output;
    Handler handler,inputHandler;
    String deviceName;
    public static BluetoothSocket bs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toast.makeText(this, "Connection complete", Toast.LENGTH_SHORT).show();

        send = findViewById(R.id.sendbtn);
        closeConnection = findViewById(R.id.closeConnectionbtn);
        msgbox = findViewById(R.id.displaymsg);
        msginput = findViewById(R.id.msg);
        deviceName = getIntent().getStringExtra("connected_device");

        if(HostConnection.bluetoothSocket == null)
            bs=  searchDevice.bluetoothSocket;
        else
            bs = HostConnection.bluetoothSocket;
        try {
            input = new DataInputStream(bs.getInputStream());
            output = new DataOutputStream(bs.getOutputStream());
            handler = new Handler();
            inputHandler = new Handler();

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = msginput.getText().toString();

                    if(msg.length() < 1){
                        Toast.makeText(Chat.this, "Enter message first ", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(Chat.this, "Sending...", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    output.writeUTF(msg);

                                }catch(Exception e) {
                                    e.printStackTrace();
                                }

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String data = msgbox.getText().toString();
                                        data += "\nME : "+msg;
                                        msgbox.setText(data);
                                        Toast.makeText(Chat.this, "Sent", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).start();

                    }
                }
            });



            closeConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Chat.this, "Closing Connection", Toast.LENGTH_SHORT).show();

                    try{
                        bs.close();
                        Intent intent = new Intent(Chat.this,MainActivity.class);
                        startActivity(intent);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


            new Thread(new Runnable() {
                @Override
                public void run() {

                    while(bs.isConnected()){

                        try{
                            String inputmsg = input.readUTF();

                            inputHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String data = msgbox.getText().toString();
                                    data += "\n"+ deviceName +" : "+inputmsg;
                                    msgbox.setText(data);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            }).start();




        } catch (IOException e) {
            Toast.makeText(this, "Cannot create streams", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}