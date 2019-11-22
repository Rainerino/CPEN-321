package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        Gson gson = new Gson();
        String json = prefs.getString("current_user", "");
        User user = gson.fromJson(json, User.class);

        username = findViewById(R.id.username);
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



        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(fm);

        viewPageAdapter.addFragment(new CalendarFragment(), "Calendar");
        viewPageAdapter.addFragment(new FriendsFragment(), "Friends");
        viewPageAdapter.addFragment(new SettingFragment(), "Setting");


        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        if(intent.hasExtra("fragment")){
            String fragment_num = intent.getStringExtra("fragment");

            if(!fragment_num.equals("1"))
            {
                fragment_num = "";
                Fragment fragment = new FriendsFragment();
                if (fragment != null) {
                            fm.beginTransaction()
                            .replace(R.id.friend_fragment, fragment).commit();
                }
            }
        }

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

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
