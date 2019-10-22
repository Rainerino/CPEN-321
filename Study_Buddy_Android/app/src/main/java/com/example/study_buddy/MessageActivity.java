package com.example.study_buddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.study_buddy.Adapter.MessageAdapter;
import com.example.study_buddy.model.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ImageView profile_img;
    TextView username;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<chat> mChat;

    RecyclerView recyclerView;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();
        final String userid = intent.getStringExtra("receiver_userid");

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
                    sendMessage("cur_user",userid, text_send.getText().toString());
                    text_send.setText("");
                    readMessage("cur_user", userid, "something");
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
}
