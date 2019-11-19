package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.study_buddy.fragments.CalendarFragment;
import com.example.study_buddy.fragments.FriendsFragment;
import com.example.study_buddy.fragments.SettingFragment;
import com.example.study_buddy.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs;

        //get current user
        prefs = getSharedPreferences("",
                MODE_PRIVATE);
        username = findViewById(R.id.username);

        int test = prefs.getInt("test", 0);
        if(test == 1) {
            String user_name = prefs.getString("test_user_name", "");
            username.setText(user_name);
        }
        else {
            Gson gson = new Gson();
            String json = prefs.getString("current_user", "");
            User user = gson.fromJson(json, User.class);
            if(json == "") {
                Intent intent = new Intent(
                        this, LoginActivity.class);
                Toast.makeText(this, "Login information expired. Please login again.",
                        Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
            else{
                username.setText(user.getFirstName());
            }
        }


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_page);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new CalendarFragment(), "Calendar");
        viewPageAdapter.addFragment(new FriendsFragment(), "Friends");
        viewPageAdapter.addFragment(new SettingFragment(), "Setting");


        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
