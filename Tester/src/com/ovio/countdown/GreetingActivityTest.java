package com.ovio.countdown;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.ovio.countdown.GreetingActivityTest \
 * com.ovio.countdown.tests/android.test.InstrumentationTestRunner
 */
public class GreetingActivityTest extends ActivityInstrumentationTestCase2<GreetingActivity> {

    public GreetingActivityTest() {
        super("com.ovio.countdown", GreetingActivity.class);
    }

    public void testMe() {
        Log.e("HELLO", "This is test!");

        assertTrue(true);
    }

}
