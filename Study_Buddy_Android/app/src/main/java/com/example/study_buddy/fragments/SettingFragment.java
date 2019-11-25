package com.example.study_buddy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.google.gson.Gson;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class SettingFragment extends Fragment {
    private TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        SharedPreferences prefs;

        //get current user
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString("current_user", "");
        User user = gson.fromJson(json, User.class);
        int test = prefs.getInt("test", 0);

        username = view.findViewById(R.id.username);
        if(test == 1){
            username.setText(prefs.getString("test_user_name", ""));
        }
        else {
            username.setText(user.getFirstName());
        }


        // Inflate the layout for this fragment
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);

        Button gsign_out = view.findViewById(R.id.LogOutButton);

        gsign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.LogOutButton:
                        onButtonClickSignOut();
                        break;
                    // ...
                    default: break;
                }
            }

            private void onButtonClickSignOut() {
                SharedPreferences cur_user = Objects.requireNonNull(getContext()).getSharedPreferences(
                        "", MODE_PRIVATE);
                SharedPreferences.Editor editor  = cur_user.edit();
                editor.putString("current_user", "");
                editor.putString("current_user_id", "");
                editor.putString("current_user_events", "");
                editor.putInt("test", 0);
                editor.apply();

                // upon sign out, go to the login page
                Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), LoginActivity.class);
                startActivity(intent);
            }


        });
    }
}
