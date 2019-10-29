package com.example.study_buddy.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.study_buddy.Adapter.NewUserAdapter;
import com.example.study_buddy.Adapter.UserAdapter;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView newUserRecyclerView;
    private TextView output;

    private UserAdapter userAdapter;
    private NewUserAdapter newUserAdapter;
    private List<User> mUsers;
    private List<User> mNewUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mUsers = new ArrayList<>();
        mNewUsers = new ArrayList<>();

        output = view.findViewById(R.id.suggest);
        recyclerView = view.findViewById(R.id.friend_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        readUsers();

        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);


        newUserRecyclerView = view.findViewById(R.id.suggested_friend_list);
        newUserRecyclerView.setHasFixedSize(true);
        newUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        readSuggestedUsers();


        newUserAdapter = new NewUserAdapter(getContext(), mNewUsers);
        newUserRecyclerView.setAdapter(newUserAdapter);

        return view;
    }

    private  void readUsers() {
        //get current User information from the database
        //for now I'll create some fake users for testing
        //get current User information from the database
        //for now I'll create some fake users for testing

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<User>> call = service.getFriends("5daf8bc2c86dec1e1069ba4c");

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for(User user: response.body()){
                    mUsers.add(user);
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

/*
        User test_user1 = new User("test_user_1", "test user1","it's");
        User test_user2 = new User("test_user_2", "test user2","not");
        User test_user3 = new User("test_user_3", "test user3","working");

        mUsers.add(test_user1);
        mUsers.add(test_user2);
        mUsers.add(test_user3);

        */


    }

    void readSuggestedUsers() {
        //for now add users for test display only
//        User suggest_user1 = new User("some", "123", "new friend 1");
//        User suggest_user2 = new User("some", "123", "new friend 2");
//        User suggest_user3 = new User("some", "123", "new friend 3");
//
//        mNewUsers.add(suggest_user1);
//        mNewUsers.add(suggest_user2);
//        mNewUsers.add(suggest_user3);
//
//        newUserAdapter = new NewUserAdapter(getContext(), mNewUsers);
//        newUserRecyclerView.setAdapter(newUserAdapter);

        final GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<String >> call = service.getSuggestFriends("5daf8bc2c86dec1e1069ba4c");
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> suggest_friend_list = response.body();
                for(String friend : suggest_friend_list){
                   Call<User> get_user_call = service.getCurrentUser(friend);
                   get_user_call.enqueue(new Callback<User>() {
                       @Override
                       public void onResponse(Call<User> call, Response<User> response) {
                           mNewUsers.add(response.body());
                           newUserAdapter = new NewUserAdapter(getContext(), mNewUsers);
                           newUserRecyclerView.setAdapter(newUserAdapter);
                       }

                       @Override
                       public void onFailure(Call<User> call, Throwable t) {

                       }
                   });
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }
}
