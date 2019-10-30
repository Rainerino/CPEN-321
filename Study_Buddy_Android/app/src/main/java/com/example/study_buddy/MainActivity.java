package com.example.study_buddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.study_buddy.Fragments.CalendarFragment;
import com.example.study_buddy.Fragments.ChatFragment;
import com.example.study_buddy.Fragments.FriendsFragment;
import com.example.study_buddy.Fragments.LoginFragment;
import com.example.study_buddy.Fragments.SettingFragment;
import com.example.study_buddy.Fragments.SignUpFragment;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitClientInstance;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ImageView profile_img;
    private TextView username;
    private SharedPreferences prefs;
    User cur_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get current user
        prefs = getSharedPreferences("",
                MODE_PRIVATE);
        final String cur_userId = prefs.getString("cur_user_id","it's not working");

        final GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<User> call = service.getCurrentUser(cur_userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                cur_user = response.body();
                username.setText(cur_user.getFirstName());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                username.setText(t.toString());
            }
        });


        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_page);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new ChatFragment(), "Chats");
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