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

import com.example.study_buddy.adapter.MessageAdapter;
import com.example.study_buddy.model.Chat;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private EditText text_send;
    private ImageButton btn_send;
    private String receivingUserId;

    private MessageAdapter messageAdapter;
    private List<Chat> mChat;

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

        setUpView();

        //set up socket
        {   //get a global socket
            try {
                mSocket = IO.socket("http://128.189.77.76:3000");
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

        mSocket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];

                        Toast.makeText(MessageActivity.this,data,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    //send the message
                   sendMessage();
                }
            }
        });

        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];

                        try {
                            //extract data from fired event

                            String senderId = data.getString("senderId");
                            String receiverId = data.getString("receiverId");
                            String message = data.getString("message");

                            if (!receiverId.equals(cur_userId)){
                                return;
                            }
                            // make instance of message

                            Chat m = new Chat(senderId, receiverId,message);

                            //add the message to the messageList

                            mChat.add(m);

                            // add the new updated list to the adapter
                            MessageAdapter messageAdapter= new MessageAdapter(
                                    MessageActivity.this, mChat, "meaning of life");

                            // notify the adapter to update the recycler view

                            messageAdapter.notifyDataSetChanged();

                            //set the adapter for the recycler view

                            recyclerView.setAdapter(messageAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    private void sendMessage() {
        mSocket.emit("messagedetection", cur_userId, receivingUserId, text_send.getText().toString());

        Chat new_chat = new Chat(cur_userId, receivingUserId, text_send.getText().toString());

        mChat.add(new_chat);

        text_send.setText("");

        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, "");

        recyclerView.setAdapter(messageAdapter);
    }

    private void setUpView() {
        intent = getIntent();
        final String receivingUserName = intent.getStringExtra("receiving_user_name");
        receivingUserId = intent.getStringExtra("receiving_user_id");

        prefs = getSharedPreferences(
                "",
                MODE_PRIVATE);
        cur_userId = prefs.getString(
                "current_user_id",
                "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ImageView profile_img = findViewById(R.id.profile_image);
        TextView username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        mChat = new ArrayList<>();

        username.setText(receivingUserName);
        username.setTextColor(Color.WHITE);
        username.setTextSize(24);

        profile_img.setImageResource(R.drawable.ic_profile_pic_name);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("chatroomDestroy", cur_userId);
        mSocket.close();
        //mSocket.off("new message", onNewMessage);
    }

}