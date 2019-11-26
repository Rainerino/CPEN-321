package com.example.study_buddy;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageTimingTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void A_checkSendMessage() {
        long before, after;
        final String testMessage = "Here is a test message";

        //go to the chat view
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        onView(withId(R.id.friend_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.message_page)).check(matches(isDisplayed()));

        //type something
        onView(withId(R.id.text_send)).perform(typeText(testMessage), closeSoftKeyboard());
        //send the message
        onView(withId(R.id.btn_send)).perform(click());
        before= System.currentTimeMillis();
        //wait for the message to display
        onView(withText(testMessage)).check(matches(isDisplayed()));
        after= System.currentTimeMillis();

        if(before - after > 500) {
            Assert.fail("takes " + (before - after)
                    + " ms to display a sent message");
        }
    }

    @Test
    public void B_checkReceiveMessage() {
        long before, after;

        final String testMessage = "Here is a test message";

        //go to the chat view
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        onView(withId(R.id.friend_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.message_page)).check(matches(isDisplayed()));
        before= System.currentTimeMillis();
        //wait for the message to display
        onView(withText(testMessage)).check(matches(isDisplayed()));
        after= System.currentTimeMillis();

        if(before - after > 500) {
            Assert.fail("takes " + (before - after)
                    + " ms to display a sent message");
        }
    }
}
