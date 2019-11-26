package com.example.study_buddy;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FragmentSwitchTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void switchFriendFragmentTest()
    {
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
    }

    @Test
    public void switchSettingFragmentTest()
    {
        onView(withText("SETTING")).check((matches(isDisplayed())));
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));
    }

    @Test
    public void switchBackTest1()
    {
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
    }

    @Test
    public void generalSwitchTest()
    {

        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));

        onView(withText("SETTING")).check((matches(isDisplayed())));
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));

        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
    }

    @Test
    public void switchBackTest2()
    {
        onView(withText("SETTING")).check((matches(isDisplayed())));
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
    }

}
