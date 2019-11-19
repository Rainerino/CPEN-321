package com.example.study_buddy;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {
    private boolean login = true;
    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);


    public void logout(){
        onView(withText("SETTING")).check((matches(isDisplayed())));
        onView(withText("SETTING")).perform(click());
        onView(withText("LOG OUT")).perform(click());
        login = false;
    }

    @Test
    public void A_InvalidEmail() {

        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
        onView(withId(R.id.login_email)).perform(typeText("123"));
        onView(withId(R.id.login_password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        onView(withText("LOGIN")).perform(click());

//        onView(withText("Yiyi")).check(matches(isDisplayed()));
        onView((withText("Invalid email address"))).check(matches(isDisplayed()));
    }

    @Test
    public void B_WrongEmail() {

        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
        onView(withId(R.id.login_email)).perform(typeText("albertyany@gmail.com"));
        onView(withId(R.id.login_password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password)).perform(typeText("12356789"), closeSoftKeyboard());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        onView(withText("LOGIN")).perform(click());

//        onView(withText("Yiyi")).check(matches(isDisplayed()));
        onView((withText("Login failed, the email doesn't not exist"))).check(matches(isDisplayed()));
    }

    @Test
    public void C_WrongPassword() {

        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
        onView(withId(R.id.login_email)).perform(typeText("albertyanyy@gmail.com"));
        onView(withId(R.id.login_password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password)).perform(typeText("12356789"), closeSoftKeyboard());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        onView(withText("LOGIN")).perform(click());

//        onView(withText("Yiyi")).check(matches(isDisplayed()));
        onView((withText("Login failed, the password doesn't not match"))).check(matches(isDisplayed()));
    }

    @Test
    public void D_SuccessfulLogin() {

        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
        onView(withId(R.id.login_email)).perform(typeText("albertyanyy@gmail.com"));
        onView(withId(R.id.login_password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        onView(withText("LOGIN")).perform(click());

        onView(withId(R.id.username)).check(matches(isDisplayed()));
    }


}
