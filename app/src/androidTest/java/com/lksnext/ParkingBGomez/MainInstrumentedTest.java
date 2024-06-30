package com.lksnext.ParkingBGomez;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.awaitility.Awaitility.await;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lksnext.ParkingBGomez.view.activity.LoginActivity;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainInstrumentedTest {

    @BeforeClass
    public static void setUpClass() {
        login();
    }

    @Before
    public void setUp() {
        // Set up the test
    }

    private static void login() {
        // Launch LoginActivity
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
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
    public void test1Reservar() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            // Navigate to the Reservar fragment
            onView(withId(R.id.nueva_reserva_extended_fab)).perform(click());

            // Select tomorrow
            onView(withId(R.id.day2)).perform(click());

            // Select motorcycle
            onView(withId(R.id.chip_motorcycle)).perform(click());

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(4, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.recyclerView))
                        .check(matches(isDisplayed())));

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(2, click())));

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(10, click())));

            onView(withId(R.id.button_reservar_continue)).perform(click());

            // Confirm reservation
            onView(withId(R.id.button_reservar_confirmar)).perform(click());
        }
    }

    @Test
    public void test2ModifyReserva() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            // Navigate to the Reservas fragment
            onView(withId(R.id.ver_todas_reservas)).perform(click());

            await()
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .pollDelay(1, TimeUnit.SECONDS)
                    .atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> onView(withId(R.id.recycler_view_reservas))
                            .check(matches(isDisplayed())));

            onView(withId(R.id.recycler_view_reservas))
                    .check(matches(isDisplayed()))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            onView(withId(R.id.reserva_edit_button)).check(matches(isEnabled())).perform(click());

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .atMost(4, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.button_reservar_continue))
                        .check(matches(isEnabled())).perform(click()));
        }
    }

    @Test
    public void test3DeleteReserva() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            // Navigate to the Reservar fragment

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.ver_todas_reservas)).perform(click()));

            await()
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> onView(withId(R.id.recycler_view_reservas))
                        .check(matches(isDisplayed())));

            onView(withId(R.id.recycler_view_reservas))
                    .check(matches(isDisplayed()))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            onView(withId(R.id.reserva_delete_button)).check(matches(isEnabled())).perform(click());

            onView(withText(R.string.confirm))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());
        }
    }

    @After
    public void tearDown() {
        // Clean up after the test
    }
}
