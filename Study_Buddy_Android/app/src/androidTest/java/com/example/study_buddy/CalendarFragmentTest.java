package com.example.study_buddy;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
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
import static org.hamcrest.Matchers.containsString;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@LargeTest
public class CalendarFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void A_GetAvailableFriends() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));
    }


    @Test
    public void C_CreateEvent() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));

        onView(withId(R.id.next_btn)).perform(click());


        onView(withId(R.id.edit_title)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_location)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_description)).perform(typeText("this is a test"), closeSoftKeyboard());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
    }

    @Test
    public void D_checkEventCreated() {
        onView(withText("this is a test")).check(matches(isDisplayed()));
    }


    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    @Test
    public void E_CreateEventWithEmptyTitle() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));

        onView(withId(R.id.next_btn)).perform(click());

        onView(withId(R.id.edit_location)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_description)).perform(typeText("this is a test"), closeSoftKeyboard());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withText("Please fill in the information")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void B_CreateEventWithEmptyLocation() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));

        onView(withId(R.id.next_btn)).perform(click());

        onView(withId(R.id.edit_title)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_description)).perform(typeText("this is a test"), closeSoftKeyboard());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withText("Please fill in the information")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void D_CreateEventWithEmptyDescription() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));

        onView(withId(R.id.next_btn)).perform(click());

        onView(withId(R.id.edit_title)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.edit_location)).perform(typeText("this is a test"), closeSoftKeyboard());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withText("Please fill in the information")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void F_CreateEventWithEmptyInformation() {
        onView(withId(R.id.calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.calendar)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.available_user_list)).check(matches(isDisplayed()));

        onView(withId(R.id.next_btn)).perform(click());

        onView(withId(R.id.submit_btn)).perform(click());
        onView(withText("Please fill in the information")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

}

