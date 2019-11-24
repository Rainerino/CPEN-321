package com.example.study_buddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    private RecyclerView newUserRecyclerView;
    private NewUserAdapter newUserAdapter;
    private List<User> mNewUsers;
    private String cur_userId;

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

        readSuggestedUsers();
    }

    public void readSuggestedUsers() {

        final GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<String >> call = service.getSuggestFriends(cur_userId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> suggest_friend_list = response.body();
                assert suggest_friend_list != null;
                Log.d("FriendFragment", suggest_friend_list.toString());
                for(String friend : suggest_friend_list){
                    Call<User> get_user_call = service.getCurrentUser(friend);
                    get_user_call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            mNewUsers.add(response.body());
                            newUserAdapter = new NewUserAdapter(getApplicationContext(), mNewUsers);
                            newUserRecyclerView.setAdapter(newUserAdapter);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Please check internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
