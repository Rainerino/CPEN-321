package com.example.study_buddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {
    ImageView profile_img;
    TextView username;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //finish();
                username.setText("should go back");
            }
        });
        */



        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        username.setText(userid);
        username.setTextColor(Color.WHITE);
        username.setTextSize(24);
        profile_img.setImageResource(R.drawable.ic_profile_pic_name);



    }
}
