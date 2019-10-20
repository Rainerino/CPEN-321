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

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

        user test_user1 = new user("test_user_1", "test user1","default");
        user test_user2 = new user("test_user_2", "test user2","default");
        user test_user3 = new user("test_user_3", "test user3","default");

        mUsers.add(test_user1);
        mUsers.add(test_user2);
        mUsers.add(test_user3);

        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);


    }

}
