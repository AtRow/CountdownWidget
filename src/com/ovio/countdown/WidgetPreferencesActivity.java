package com.ovio.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.DefaultOptions;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.util.Util;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesActivity extends Activity {

    private static final String TAG = Logger.PREFIX + "PrefsActivity";

    private Context self = this;

    private int appWidgetId;

    private PreferencesManager prefManager;

    private WidgetPreferencesManager widgetManager;

    private WidgetOptions widgetOptions;

    DefaultOptions options;

    private EditText editText;

    private boolean isOkPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "Created Widget Preferences Activity");

        setContentView(R.layout.preferences);

        isOkPressed = false;
        appWidgetId = getAppWidgetId();
        widgetOptions = new WidgetOptions();
        options = new DefaultOptions();

        setResultCanceled();

        editText = (EditText) findViewById(R.id.editText);

        prefManager = PreferencesManager.getInstance(self);
        widgetManager = WidgetPreferencesManager.getInstance(self);

        loadDefaultPreferences();
        startWidgetService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // There's a bug somewhere, see
        // http://stackoverflow.com/questions/4393144/widget-not-deleted-when-passing-result-canceled-as-result-for-configuration-acti
        Logger.i(TAG, "Stopped");
        if (!isOkPressed) {
            Logger.i(TAG, "Ok wasn't pressed, forcing removal of a widget %s", appWidgetId);
            forceRemoveWidget(appWidgetId);
        }
        finish();
    }

    public void onOk(View view) {
        Logger.d(TAG, "OK button clicked");

        saveDefaultPreferences();
        saveWidgetPreferences();

        sendToWidgetService();

        setResultOk();

        Logger.i(TAG, "Finished Widget Preferences Activity with RESULT_OK");
        finish();
    }

    public void onCancel(View view) {
        Logger.d(TAG, "Cancel button clicked");

        Logger.i(TAG, "Finished Widget Preferences Activity with RESULT_CANCELED");
        finish();
    }

    private void startWidgetService() {
        Logger.i(TAG, "Sending START intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.START);
        startService(widgetServiceIntent);
    }

    private void sendToWidgetService() {
        Logger.i(TAG, "Sending UPDATED intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.UPDATED);

        Bundle extras = new Bundle();
        extras.putSerializable(WidgetService.OPTIONS, widgetOptions);

        widgetServiceIntent.putExtras(extras);
        Logger.d(TAG, "Intent Extras: %s", widgetOptions);

        startService(widgetServiceIntent);
    }

    private Intent getResultIntent() {
        return new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    }

    private void setResultOk() {
        setResult(RESULT_OK, getResultIntent());
        isOkPressed = true;
    }

    private void setResultCanceled() {
        setResult(RESULT_CANCELED, getResultIntent());
        isOkPressed = false;
    }

    private int getAppWidgetId() {
        Logger.d(TAG, "Getting new appWidgetId");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Logger.i(TAG, "Got AppWidgetId: %s", id);

            if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
                Logger.e(TAG, "No widget id specified in starting Intent. Exiting.");
                finish();
            }

            return id;

        } else {
            Logger.e(TAG, "Can't find appWidgetId in a starting Intent's extras; Extras == null!");
            finish();
            return AppWidgetManager.INVALID_APPWIDGET_ID;
        }
    }

    private void forceRemoveWidget(int appWidgetId) {
        AppWidgetHost host = new AppWidgetHost(self, 1);
        host.deleteAppWidgetId(appWidgetId);
    }

    private void saveWidgetPreferences() {
        Logger.i(TAG, "Saving Widget Options");

        //TODO

        widgetOptions.widgetId = appWidgetId;
        widgetOptions.title = editText.getText().toString();
        widgetOptions.timestamp = System.currentTimeMillis() + (1000 * 10 * 60 * 60);
        widgetOptions.upward = true;
        widgetOptions.enableSeconds = false;
        widgetOptions.repeatingPeriod = 0L;

        Logger.d(TAG, "Widget Options: %s", widgetOptions);

        widgetManager.add(widgetOptions);

        Logger.d(TAG, "Finished saving Widget Options");
    }

    private void saveDefaultPreferences() {
        Logger.i(TAG, "Saving Global Options");

        List<Integer> list = Util.toIntegerList(options.savedWidgets);
        list.add(appWidgetId);
        int[] savedWidgets = Util.toIntArray(list);

        options.savedWidgets = savedWidgets;
        options.enableTime = false;

        Logger.d(TAG, "Global Options: %s", options);

        prefManager.saveDefaultPrefs(options);

        Logger.d(TAG, "Finished saving Global Options");
    }

    private void loadDefaultPreferences() {
        Logger.i(TAG, "Loading Global Options");

        options = prefManager.loadDefaultPrefs();

        Logger.d(TAG, "Global Options: %s", options);

        editText.setText("empty");
    }

}