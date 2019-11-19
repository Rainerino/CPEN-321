package com.example.study_buddy;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class PageSwitchTimingTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void switchFriendFragmentTest() {

        onView(withText("FRIENDS")).check((matches(isDisplayed())));

        long before= System.currentTimeMillis();
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        long after= System.currentTimeMillis();

        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after) + " ms to switch a fragment");
        }

    }

    @Test
    public void switchSettingFragmentTest() {
        onView(withText("SETTING")).check((matches(isDisplayed())));

        long before = System.currentTimeMillis();
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));
        long after = System.currentTimeMillis();

        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after) + " ms to switch a fragment");
        }
    }

    @Test
    public void switchBackTest1() {
        long before, after;

        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after) + " ms to switch a fragment");
        }

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after) + " ms to switch a fragment");
        }
    }

    @Test
    public void generalSwitchTest() {
        long before, after;

        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after) + " ms to switch a fragment");
        }

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }

        onView(withText("SETTING")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }

        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }
    }

    @Test
    public void switchBackTest2() {
        long before, after;

        onView(withText("SETTING")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("SETTING")).perform(click());
        onView((withId(R.id.setting_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }

        onView(withText("CALENDAR")).check((matches(isDisplayed())));
        before = System.currentTimeMillis();
        onView(withText("CALENDAR")).perform(click());
        onView((withId(R.id.calendar_fragment))).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + Long.toString(before - after)
                    + " ms to switch a fragment");
        }
    }

        @Test
    public void ScheduleMeetingPopup() {
        long before, after;

        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        before = System.currentTimeMillis();
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        onView(withId(R.id.schedule_meeting_startup)).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + (before - after)
                    + " ms to switch a fragment");
        }


        before = System.currentTimeMillis();
        onView(withId(R.id.next_btn)).perform(click());
        onView(withId(R.id.schedule_meeting_detail)).check(matches(isDisplayed()));
        after = System.currentTimeMillis();
        if(before - after > 100) {
            Assert.fail("takes " + (before - after)
                    + " ms to switch a fragment");
        }
    }

    
}
