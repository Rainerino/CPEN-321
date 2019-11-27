package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {
    private Group cur_group;
    private User currentUser;

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

        /** Get currentUser **/
        SharedPreferences prefs;
        prefs = getSharedPreferences("", MODE_PRIVATE);
        String json = prefs.getString("current_user", "");
        currentUser = gson.fromJson(json, User.class);

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

    private void tryDelete() {
        SharedPreferences prefs;

        //get current user
        prefs = getSharedPreferences("", MODE_PRIVATE);

        String user_id = prefs.getString("current_user_id", "");
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Group> call = service.deleteGroup(currentUser.getJwt(), user_id, cur_group.getId());
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if(response.isSuccessful()){
                    goBackToMain();
                }
                else {
                    Toast.makeText(getApplicationContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            case R.id.leave_group :
                tryDelete();
                return true;
        }
        return true;
    }
}
