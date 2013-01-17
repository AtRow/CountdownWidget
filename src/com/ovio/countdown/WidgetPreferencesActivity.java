package com.ovio.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.ovio.countdown.preferences.DefaultPrefs;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesActivity extends Activity {

    private static final String TAG = "PREFS";

    private static final String GLOBAL_PREFS = "global";

    private Context self = this;

    private int appWidgetId;

    private Intent resultIntent;

    private PreferencesManager prefManager;

    private WidgetPreferencesManager widgetManager;

    private WidgetOptions widgetOptions;

    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting WidgetPreferencesActivity");

        appWidgetId = getAppWidgetId();
        resultIntent = getResultIntent();
        widgetOptions = new WidgetOptions();

        setResult(RESULT_CANCELED, resultIntent);

        Log.d(TAG, "Applying layout");
        setContentView(R.layout.preferences);

        editText = (EditText) findViewById(R.id.editText);

        prefManager = new PreferencesManager(self);
        widgetManager = new WidgetPreferencesManager(self);

        loadDefaultPreferences();
    }

    public void onOk(View view) {
        Log.d(TAG, "OK button clicked");

        saveDefaultPreferences();
        saveWidgetPreferences();

        widgetManager.update(appWidgetId);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onCancel(View view) {
        Log.d(TAG, "Cancel button clicked");

        finish();
    }


    private Intent getResultIntent() {
        return new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    }

    private int getAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Log.d(TAG, "Got AppWidgetId = " + id);
            return id;

        } else {
            // TODO: what to do?
            finish();
            return AppWidgetManager.INVALID_APPWIDGET_ID;
        }
    }

    private void saveWidgetPreferences() {

        //TODO

        widgetOptions.widgetId = appWidgetId;
        widgetOptions.title = editText.getText().toString();
        widgetOptions.timestamp = System.currentTimeMillis() + (1000 * 10 * 60);
        widgetOptions.upward = true;
        widgetOptions.enableSeconds = false;
        widgetOptions.repeatingPeriod = 0L;

        widgetManager.add(widgetOptions);
    }

    private void saveDefaultPreferences() {
        Log.d(TAG, "Saving preferences");

        DefaultPrefs prefs = new DefaultPrefs();

        prefs.enableTime = false;

        prefManager.saveDefaultPrefs(GLOBAL_PREFS, prefs);
    }

    private void loadDefaultPreferences() {
        Log.d(TAG, "Loading preferences");

        DefaultPrefs prefs = prefManager.loadDefaultPrefs(GLOBAL_PREFS);

        editText.setText("empty");
    }

}