package com.ovio.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.ovio.countdown.preferences.DefaultOptions;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesActivity extends Activity {

    private static final String TAG = "PREFS";

    private Context self = this;

    private int appWidgetId;

    private Intent resultIntent;

    private PreferencesManager prefManager;

    private WidgetPreferencesManager widgetManager;

    private WidgetOptions widgetOptions;

    DefaultOptions options;

    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting WidgetPreferencesActivity");

        appWidgetId = getAppWidgetId();
        resultIntent = getResultIntent();
        widgetOptions = new WidgetOptions();
        options = new DefaultOptions();

        setResult(RESULT_CANCELED, resultIntent);

        Log.d(TAG, "Applying layout");
        setContentView(R.layout.preferences);

        editText = (EditText) findViewById(R.id.editText);

        prefManager = PreferencesManager.getInstance(self);
        widgetManager = WidgetPreferencesManager.getInstance(self);

        loadDefaultPreferences();
        startWidgetService();
    }

    public void onOk(View view) {
        Log.d(TAG, "OK button clicked");

        saveDefaultPreferences();
        saveWidgetPreferences();

        sendToWidgetService();

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onCancel(View view) {
        Log.d(TAG, "Cancel button clicked");

        finish();
    }

    private void startWidgetService() {
        Intent widgetServiceIntent = new Intent(WidgetService.START);
        startService(widgetServiceIntent);
    }

    private void sendToWidgetService() {
        Intent widgetServiceIntent = new Intent(WidgetService.UPDATED);


        Bundle extras = new Bundle();
        extras.putSerializable(WidgetService.OPTIONS, widgetOptions);

        widgetServiceIntent.putExtras(extras);
        startService(widgetServiceIntent);
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

        int cnt = options.savedWidgets.length;

        int[] savedWidgets = new int[cnt + 1];
        savedWidgets[cnt] = appWidgetId;

        options.savedWidgets = savedWidgets;
        options.enableTime = false;

        prefManager.saveDefaultPrefs(options);
    }

    private void loadDefaultPreferences() {
        Log.d(TAG, "Loading preferences");

        options = prefManager.loadDefaultPrefs();

        editText.setText("empty");
    }

}