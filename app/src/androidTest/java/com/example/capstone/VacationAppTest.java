package com.example.capstone;


import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.capstone.Activities.VacationList;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4ClassRunner.class)
public class VacationAppTest {

    @Rule
    public ActivityScenarioRule<VacationList> activityScenarioRule = new ActivityScenarioRule<>(VacationList.class);

    @Test
    public void testCRUDOperations() {

        //Save Vacation
        ActivityScenario<VacationList> scenario = ActivityScenario.launch(VacationList.class);
        Espresso.onView(withId(R.id.floatingActionButton)).perform(click());
        Espresso.onView(withId(R.id.vacationname)).perform(typeText("Test Vacation"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.hotelname)).perform(typeText("Test Hotel"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.startdate)).perform(typeText("02/12/2024"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.enddate)).perform(typeText("02/13/2024"), closeSoftKeyboard());
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withText("Save Vacation")).perform(click());
        Espresso.onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        Espresso.onView(withText("Test Vacation")).check(matches(isDisplayed()));

        //Edit Vacation
        Espresso.onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.onView(withId(R.id.vacationname)).perform(clearText(), typeText("Edited Vacation"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.hotelname)).perform(clearText(), typeText("Edited Hotel"), closeSoftKeyboard());
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Espresso.onView(withText("Save Vacation")).perform(click());
        Espresso.onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        Espresso.onView(withText("Edited Vacation")).check(matches(isDisplayed()));

        //Delete Vacation
        Espresso.onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Espresso.onView(withText("Delete Vacation")).perform(click());
        Espresso.onView(withText("Edited Vacation")).check(doesNotExist());
    }

}
