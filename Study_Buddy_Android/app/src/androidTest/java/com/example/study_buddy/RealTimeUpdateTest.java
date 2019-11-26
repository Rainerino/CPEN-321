package com.example.study_buddy;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

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
public class RealTimeUpdateTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void A_CreateEvent() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.next_btn)).perform(click());

        onView(withId(R.id.edit_title)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_location)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_description)).perform(typeText("this is a test"), closeSoftKeyboard());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));

        onView(withText("this is a test")).check(matches(isDisplayed()));
    }

    @Test
    public void B_CheckEventCreated() {
        onView(withText("this is a test")).check(matches(isDisplayed()));

    }

    @Test
    public void C_ChatTest() {
        //go to the chat view
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        onView(withId(R.id.friend_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.message_page)).check(matches(isDisplayed()));

        //type something
        onView(withId(R.id.text_send)).perform(typeText("test"), closeSoftKeyboard());
        //send the message
        onView(withId(R.id.btn_send)).perform(click());

        //check if our message pops up or not
        onView(withText("test")).check(matches(isDisplayed()));
    }

    @Test
    public void D_CheckMessageSent() {
        //go to the chat view
        onView(withText("FRIENDS")).check((matches(isDisplayed())));
        onView(withText("FRIENDS")).perform(click());
        onView((withId(R.id.friend_fragment))).check(matches(isDisplayed()));
        onView(withId(R.id.friend_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.message_page)).check(matches(isDisplayed()));

        //check if our message pops up or not
        onView(withText("test")).check(matches(isDisplayed()));
    }


}
