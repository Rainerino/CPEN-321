package com.example.study_buddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.study_buddy.fragments.LoginFragment;
import com.example.study_buddy.fragments.SignUpFragment;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        ViewPager viewPager = findViewById(R.id.viewPager);

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new SignUpFragment());
        viewPager.setAdapter(pagerAdapter);

    }

    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        public AuthenticationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return fragmentList.get(i);
        }

        @Override
        public int getCount() {

            return fragmentList.size();
        }

        private void addFragmet(Fragment fragment) {

            fragmentList.add(fragment);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target)
                && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((!"".equals(str))
                && (str != null)
                && (str.matches("^[a-zA-Z]*$")));
    }
}