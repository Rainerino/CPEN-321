package com.example.study_buddy.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.study_buddy.MainActivity;

public class NotificationAccept extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //
        Intent intent = new Intent(
                NotificationAccept.this, MainActivity.class);
        startActivity(intent);
    }
}
