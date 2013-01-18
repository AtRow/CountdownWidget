package com.ovio.countdown;

import android.app.Activity;
import android.os.Bundle;
import com.ovio.countdown.log.Logger;

public class GreetingActivity extends Activity {

    private static final String TAG = Logger.PREFIX + "Greeting";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "Created Greeting Activity");

        setContentView(R.layout.greeting);
    }
}
