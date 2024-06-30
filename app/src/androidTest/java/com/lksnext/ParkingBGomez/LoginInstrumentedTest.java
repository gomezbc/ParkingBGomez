package com.lksnext.ParkingBGomez;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.ParkingBGomez.view.activity.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        // Set up the test
    }

    @Test
    public void testLogin() {
        // Test the login functionality
        final String email = "test@test.com";
        final String password = "testpassword";
        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));

        onView(withId(R.id.email)).check(matches(withText(email)));
        onView(withId(R.id.password)).check(matches(withText(password)));

        onView(withId(R.id.login)).perform(click());
    }

    @Test
    public void testSignUp() {
        // Test the login functionality
        final String email = "test@test.com";
        final String password = "testpassword";

        onView(withId(R.id.register_text)).perform(click());

        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));

        onView(withId(R.id.email)).check(matches(withText(email)));
        onView(withId(R.id.password)).check(matches(withText(password)));

        onView(withId(R.id.sign_up_button)).perform(click());
    }

    /*@Test
    public void testForgotPassword() {
        // Test the login functionality
        final String email = "test@test.com";

        onView(withId(R.id.forgot_password)).perform(click());

        onView(withId(R.id.email)).perform(typeText(email));

        onView(withId(R.id.email)).check(matches(withText(email)));

        // Confirm the reset
        onView(withId(R.id.password_reset_continue_button)).perform(click());

        // Click continue in the last fragment
        onView(withId(R.id.password_reset_continue_button)).perform(click());
    }*/

    @After
    public void tearDown() {
        // Clean up after the test
        scenarioRule.getScenario().close();
        // Sign out after the test
        FirebaseAuth.getInstance().signOut();
    }
}
