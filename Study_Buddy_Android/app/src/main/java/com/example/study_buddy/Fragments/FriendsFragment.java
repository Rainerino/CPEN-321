package com.example.study_buddy.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.study_buddy.Adapter.UserAdapter;
import com.example.study_buddy.R;
import com.example.study_buddy.model.user;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.Types.NULL;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<user> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUsers();

        return view;
    }

    private  void readUsers() {
        //get current user information from the database
        //for now I'll create some fake users for testing
        /*
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<user>> call = service.getFriends("something"); /***change it later

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                List<user> friend_list = response.body();
                for(user user:friend_list){
                    mUsers.add(user);
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                user test_user1 = new user("test_user_1", "test user1","it's");
                user test_user2 = new user("test_user_2", "test user2","not");
                user test_user3 = new user("test_user_3", "test user3","working");

                mUsers.add(test_user1);
                mUsers.add(test_user2);
                mUsers.add(test_user3);
            }
        });
        */

        user test_user1 = new user("test_user_1", "test user1","it's");
        user test_user2 = new user("test_user_2", "test user2","not");
        user test_user3 = new user("test_user_3", "test user3","working");

        mUsers.add(test_user1);
        mUsers.add(test_user2);
        mUsers.add(test_user3);

        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);
    }

}
