package com.example.study_buddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.study_buddy.model.Group;
import com.google.gson.Gson;

public class GroupActivity extends AppCompatActivity {
    private Group cur_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        final String receiving_group = intent.getStringExtra("group_into");
        Gson gson = new Gson();
        cur_group = gson.fromJson(receiving_group, Group.class);
        TextView group_name = findViewById(R.id.group_name);
        ImageView img = findViewById(R.id.profile_image);
        img.setImageResource(R.mipmap.ic_group_default_round);
        group_name.setText(cur_group.getGroupName());

        /** Toolbar setup **/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.group_calendar :
                Intent intent = new Intent(this, GroupCalendarActivity.class);
                Gson gson = new Gson();
                String group_info = gson.toJson(cur_group);
                intent.putExtra("group_into", group_info);
                startActivity(intent);
                return true;
            case R.id.create_group :
                return true;
        }
        return true;
    }
}
