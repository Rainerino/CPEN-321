package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.study_buddy.App.getContext;

public class GroupActivity extends AppCompatActivity {
    private Group cur_group;
    private User currentUser;
    private PopupWindow popupWindow;
    private PopupWindow deletePopup;
    private RecyclerView recyclerView;
    private SelectUserAdapter selectUserAdapter;
    private List<User> mFriends;
    private List<User> filteredUsers;
    private List<User> mSelectedUsers;
    private final GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);



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
        prefs = getSharedPreferences("",
                MODE_PRIVATE);

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

        /** Create the popup window **/
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(800);
        popupWindow.setFocusable(true);

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
        prefs = getSharedPreferences("",
                MODE_PRIVATE);

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

    private void addMemberPopup(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_member_popup, null);
        popupWindow.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        EditText search_bar = popupView.findViewById(R.id.search_user);
        Button create_btn;
        mFriends = new ArrayList<>();
        readUsers();
        mSelectedUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();


        //editText = popupView.findViewById(R.id.search_user);
        create_btn = popupView.findViewById(R.id.next_btn);
        recyclerView = popupView.findViewById(R.id.available_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        selectUserAdapter = new SelectUserAdapter(getContext(), filteredUsers);
        recyclerView.setAdapter(selectUserAdapter);




        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                if(mSelectedUsers.isEmpty()) {
                    Toast.makeText(getContext(), "Please select at least one member to add",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    popupWindow.dismiss();
                    addMember();
                }
            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    InitUser();
                }
                else {
                    searchUser(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private  void readUsers() {

        Call<List<User>> call = service.getFriends(currentUser.getJwt(),currentUser.getid());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mFriends = response.body();
                if(mFriends != null) {
                    for(User user: mFriends){
                        filteredUsers.add(user);
                    }
                    selectUserAdapter = new SelectUserAdapter(getContext(), filteredUsers);
                    recyclerView.setAdapter(selectUserAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Can't get friends. Please check internet connection",
                        Toast.LENGTH_LONG).show();
                Log.e("Get Friend List", "onFailure: " + t.toString() );
            }
        });

    }

    private void InitUser() {
        filteredUsers.clear();
        if(!mFriends.isEmpty()){
            for(User user : mFriends){
                filteredUsers.add(user);
            }
        }
        selectUserAdapter.notifyDataSetChanged();
    }

    private void searchUser(String s) {
        filteredUsers.clear();
        for(User user : mFriends) {
            if(user.getFirstName().toLowerCase().contains(s.toLowerCase())){
                filteredUsers.add(user);
            }
        }

        selectUserAdapter.notifyDataSetChanged();
    }

    private void addMember() {
        for(User user : mSelectedUsers) {
            Call<Group> addCall = service.addGroup(currentUser.getJwt(), user.getid(), cur_group.getId());
            addCall.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Invitation sent",
                                Toast.LENGTH_LONG).show();
                        //TODO: NOTIFY MEMBER
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(getContext(), t.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
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
            case R.id.add_member :
                addMemberPopup(findViewById(R.id.group_activity));
                return true;
        }
        return true;
    }
}
