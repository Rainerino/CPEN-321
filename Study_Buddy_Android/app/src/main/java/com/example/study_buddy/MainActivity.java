package com.example.study_buddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.study_buddy.Fragments.CalendarFragment;
import com.example.study_buddy.Fragments.ChatFragment;
import com.example.study_buddy.Fragments.FriendsFragment;
import com.example.study_buddy.model.user;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ImageView profile_img;
    TextView username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        //user cur_user;
        //user = getCurrentUser();
        //username.setText(cur_user.getUsername());

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_page);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new ChatFragment(), "Chats");
        viewPageAdapter.addFragment(new CalendarFragment(), "Calendar");
        viewPageAdapter.addFragment(new FriendsFragment(), "Friends");

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
