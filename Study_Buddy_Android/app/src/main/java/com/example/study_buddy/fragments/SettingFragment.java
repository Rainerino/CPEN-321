package com.example.study_buddy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.study_buddy.LoginActivity;
import com.example.study_buddy.R;
import java.util.Objects;


public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button gsign_out = view.findViewById(R.id.LogOutButton);

        gsign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.LogOutButton:
                        onButtonClickSignOut();
                        break;
                    // ...
                }
            }

            private void onButtonClickSignOut() {
                SharedPreferences cur_user = Objects.requireNonNull(getContext()).getSharedPreferences(
                        "", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor  = cur_user.edit();
                editor.putString("current_user_id", "");
                editor.apply();

                // upon sign out, go to the login page
                Intent intent = new Intent(Objects.requireNonNull(getView()).getContext(), LoginActivity.class);
                startActivity(intent);
            }


        });
    }
}
