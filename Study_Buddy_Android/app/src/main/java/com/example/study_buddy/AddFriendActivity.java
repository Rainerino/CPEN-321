package com.example.study_buddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.adapter.NewUserAdapter;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {
    final GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
    private RecyclerView newUserRecyclerView;
    private RecyclerView searchedUserRecyclerView;
    private NewUserAdapter newUserAdapter;
    private NewUserAdapter searchedUserAdapter;
    private List<User> mNewUsers;
    private String cur_userId;
    private List<User> allUsers;
    private List<String> allEmails;
    private List<User> filteredUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        SharedPreferences sharedPref = Objects.requireNonNull(getSharedPreferences(
                "",MODE_PRIVATE));
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        User currentUser = gson.fromJson(user, User.class);
        cur_userId = currentUser.getid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AddFriendActivity.this,MainActivity.class);
//
//                intent.putExtra("fragment","1");
//                startActivity(intent);
//            }
//        });

        mNewUsers = new ArrayList<>();
        newUserRecyclerView = findViewById(R.id.suggested_friend_list);
        newUserRecyclerView.setHasFixedSize(true);
        newUserRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        newUserAdapter = new NewUserAdapter(this, mNewUsers, this);
        newUserRecyclerView.setAdapter(newUserAdapter);

        filteredUser = new ArrayList<>();
        allEmails = new ArrayList<>();
        searchedUserRecyclerView = findViewById(R.id.searched_user_list);
        searchedUserRecyclerView.setHasFixedSize(true);
        searchedUserRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        searchedUserAdapter = new NewUserAdapter(this, filteredUser, this);
        searchedUserRecyclerView.setAdapter(searchedUserAdapter);

        clearUser();

        getAllUser();

        readSuggestedUsers();

        EditText search_bar = findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    clearUser();
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

    private void searchUser(String searchString) {
        filteredUser.clear();
        for(String email : allEmails) {
            if(email.toLowerCase().contains(searchString.toLowerCase())){
                filteredUser.add(allUsers.get(allEmails.indexOf(email)));
            }
        }
        searchedUserAdapter.notifyDataSetChanged();
    }


    private void clearUser() {
        filteredUser.clear();
        searchedUserAdapter.notifyDataSetChanged();
    }

    private void getAllUser() {
        Call<List<User>> allUserCall = service.getAllUser();
        allUserCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                allUsers = response.body();
                for(User user : allUsers){
                    allEmails.add(user.getEmail());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }
    private void readSuggestedUsers() {

        Call<List<User >> call = service.getSuggestFriends(cur_userId);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()) {
                    mNewUsers = response.body();
                    newUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG).show();
                Log.e("Get Suggest Friends", "onFailure: " + t.toString() );
            }
        });
    }

    public void addUserRequest(User user) {
        /** Send notification **/

    }

}
