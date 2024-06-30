package com.lksnext.ParkingBGomez;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.awaitility.Awaitility.await;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.ParkingBGomez.view.activity.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class LoginInstrumentedTest {

    @Before
    public void setUp() {
        // Set up the test
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void test1Login() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            // Test the login functionality
            final String email = "test@test.com";
            final String password = "testpassword";
            onView(withId(R.id.email)).perform(typeText(email));
            onView(withId(R.id.password)).perform(typeText(password));

            onView(withId(R.id.email)).check(matches(withText(email)));
            onView(withId(R.id.password)).check(matches(withText(password)));

            onView(withId(R.id.login)).perform(click());
        }
    }

    @Test
    public void test2SignUp() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
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
    }

    @Test
    public void test3ForgotPassword() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            // Test the forgot password functionality
            final String email = "test@test.com";

            onView(withId(R.id.forgot_password)).perform(click());

            onView(withId(R.id.email)).perform(typeText(email));

            onView(withId(R.id.email)).check(matches(withText(email)));

            // Confirm the reset
            onView(withId(R.id.password_reset_continue_button)).perform(click());

            // Click continue in the last fragment
            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.password_reset_continue_button))
                        .check(matches(isDisplayed())));
            onView(withId(R.id.password_reset_continue_button)).perform(click());
        }
    }

    @After
    public void tearDown() {
        // Sign out after the test
        FirebaseAuth.getInstance().signOut();
    }
}
