package com.example.study_buddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.study_buddy.Adapter.MessageAdapter;
import com.example.study_buddy.model.chat;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private ImageView profile_img;
    private TextView username;

    private ImageButton btn_send;
    private EditText text_send;

    private MessageAdapter messageAdapter;
    private List<chat> mChat;

    private RecyclerView recyclerView;

    private Intent intent;

    private Socket mSocket;
    boolean isConnected;
    private String cur_userId;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        intent = getIntent();
        final String userid = intent.getStringExtra("receiver_userid");
        prefs = getSharedPreferences("",
                MODE_PRIVATE);
        cur_userId = prefs.getString("cur_user_id","it's not working");

        //set up socket
        {   //get a global socket
            try {
                mSocket = IO.socket("http://ec2-18-191-87-244.us-east-2.compute.amazonaws.com:3000");
                mSocket.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        mSocket.emit("join", cur_userId);

        mSocket.on("userjoinedthechat", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String data = (String) args[0];
                                // get the extra data from the fired event and display a toast
                                Toast.makeText(MessageActivity.this, data, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        mChat = new ArrayList<>();

        username.setText(userid);
        username.setTextColor(Color.WHITE);
        username.setTextSize(24);
        profile_img.setImageResource(R.drawable.ic_profile_pic_name);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    //send the message
                    mSocket.emit("messagedetection",cur_userId,text_send.getText().toString());
                    sendMessage(cur_userId,userid, text_send.getText().toString());
                    text_send.setText("");
                    readMessage(cur_userId, userid, "something");
                }
            }
        });

        /*
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //finish();
                username.setText("should go back");
            }
        });
        */
    }

    private void sendMessage(String sender, String receiver, String message){
        //wants to store the message information to the database and signal the socket
        //for now ill just add it into the chat list and hopefully it will display


        chat new_chat = new chat(sender,receiver,message);
        mChat.add(new_chat);


        //store the message to our database
    }

    private void readMessage(String myid, String userid, String imgURL){
        //read and display the messages
        messageAdapter = new MessageAdapter(MessageActivity.this,mChat,"someURL");
        recyclerView.setAdapter(messageAdapter);

    }
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(!isConnected) {
//                        if(null!=cur_user)
//                            mSocket.emit("add User", cur_user);
//                        Toast.makeText(getApplicationContext(),
//                                "connected to the socket", Toast.LENGTH_LONG).show();
//                        isConnected = true;
//                    }
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onDisconnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
//        }
//    };
}
