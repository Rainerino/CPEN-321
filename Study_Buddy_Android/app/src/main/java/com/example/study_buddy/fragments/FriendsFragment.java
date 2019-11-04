package com.example.study_buddy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.study_buddy.adapter.NewUserAdapter;
import com.example.study_buddy.adapter.UserAdapter;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView newUserRecyclerView;


    private UserAdapter userAdapter;
    private NewUserAdapter newUserAdapter;
    private List<User> mUsers;
    private List<User> mNewUsers;
    private SharedPreferences prefs;
    private String cur_userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);
        cur_userId = prefs.getString("current_user_id","");

        mUsers = new ArrayList<>();
        mNewUsers = new ArrayList<>();

        TextView output = view.findViewById(R.id.suggest);
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
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<User>> call = service.getFriends(cur_userId);

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
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void readSuggestedUsers() {

        final GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<String >> call = service.getSuggestFriends(cur_userId);
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
                           Toast.makeText(getContext(), "Please check internet connection",
                                   Toast.LENGTH_LONG).show();
                       }
                   });
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
