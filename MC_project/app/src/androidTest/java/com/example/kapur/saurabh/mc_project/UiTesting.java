package com.example.kapur.saurabh.mc_project;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiTesting {
    private boolean isliked;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(
            HomeActivity.class);

    @Before
    public void initial() {
        isliked = true;

    }


    @Test
    public void checkLikebutton() {

        // Type text and then press the button.
        onView(withId(R.id.like_btn)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.like_btn)).check(matches(isClickable()));

    }
    @Test
    public void checkfollowButton()
    {
        onView(withId(R.id.follow_button)).perform(click());

        onView(withId(R.id.follow_button)).check(matches(isClickable()));
    }


}


