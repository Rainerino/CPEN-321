package com.example.study_buddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
    public static final String CHANNEL_ID_1 = "first channel";
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

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new CalendarFragment(), "Calendar");
        viewPageAdapter.addFragment(new FriendsFragment(), "Friends");
        viewPageAdapter.addFragment(new SettingFragment(), "Setting");


        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            // crate a channel with high priority
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_1, name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

//            // Create an Intent for the activity you want to start
//            Intent resultIntent = new Intent(this, MainActivity.class);
//            // Create the TaskStackBuilder and add the intent, which inflates the back stack
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addNextIntentWithParentStack(resultIntent);
//            // Get the PendingIntent containing the entire back stack
//            PendingIntent resultPendingIntent =
//                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
