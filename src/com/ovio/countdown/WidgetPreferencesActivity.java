package com.ovio.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.DefaultOptions;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.service.WidgetService;
import com.ovio.countdown.util.Util;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesActivity extends Activity {

    private static final String TAG = Logger.PREFIX + "PrefsActivity";

    private final Context self = this;

    private Integer appWidgetId;

    private PreferencesManager prefManager;

    private WidgetPreferencesManager widgetManager;

    private WidgetOptions widgetOptions;

    private DefaultOptions options;

    // UI

    private EditText titleEditText;

    private DatePicker datePicker;

    private TimePicker timePicker;

    private CheckBox secondsCheckBox;

    private boolean doForceRemoveOnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "Created Widget Preferences Activity");

        setContentView(R.layout.preferences);

        initTabHost();

        setResultCanceled();

        prefManager = PreferencesManager.getInstance(self);
        widgetManager = WidgetPreferencesManager.getInstance(self);

        obtainFormElements();

        doForceRemoveOnCancel = true;

        appWidgetId = getAppWidgetId();
        widgetOptions = getWidgetOptions(appWidgetId);

        // Enable force remove widget if it's not stored
        doForceRemoveOnCancel = !widgetOptions.isValid();

        options = loadDefaultOptions();

        applyOptions(options, widgetOptions);
    }

    private void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.defaultTabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setIndicator(getString(R.string.tab_manual));
        tabSpec.setContent(R.id.tabManual);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.tab_google));
        tabSpec.setContent(R.id.tabGoogle);
        tabHost.addTab(tabSpec);

        //tabHost.setCurrentTabByTag("tag1");

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // There's a bug somewhere, see
        // http://stackoverflow.com/questions/4393144/widget-not-deleted-when-passing-result-canceled-as-result-for-configuration-acti
        Logger.i(TAG, "Stopped");
        if (doForceRemoveOnCancel) {
            Logger.i(TAG, "Ok wasn't pressed, forcing removal of a widget %s", getAppWidgetId());
            forceRemoveWidget(getAppWidgetId());
        }
        finish();
    }

    public void onOk(View view) {
        Logger.d(TAG, "OK button clicked");

        saveDefaultPreferences();
        saveWidgetOptions();

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
        return new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
    }

    private void setResultOk() {
        setResult(RESULT_OK, getResultIntent());
        doForceRemoveOnCancel = false;
    }

    private void setResultCanceled() {
        setResult(RESULT_CANCELED, getResultIntent());
        doForceRemoveOnCancel = true;
    }

    private void obtainFormElements() {
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        timePicker.setIs24HourView( DateFormat.is24HourFormat(this) );

        secondsCheckBox = (CheckBox) findViewById(R.id.secondsCheckBox);
    }

    private int getAppWidgetId() {

        if (appWidgetId == null) {
            Logger.d(TAG, "Getting new appWidgetId");

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                appWidgetId  = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                Logger.i(TAG, "Got AppWidgetId: %s", appWidgetId );

                if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                    Logger.e(TAG, "Invalid widget id specified in starting Intent. Exiting.");
                    Toast.makeText(this, "Widget not found", Toast.LENGTH_LONG).show();
                    finish();
                }

            } else {
                Logger.e(TAG, "Can't find appWidgetId in a starting Intent's extras; Extras == null!");
                Toast.makeText(this, "Widget not found", Toast.LENGTH_LONG).show();
                finish();
                appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            }
        }
        return appWidgetId;
    }

    private void forceRemoveWidget(int appWidgetId) {
        AppWidgetHost host = new AppWidgetHost(self, 1);
        host.deleteAppWidgetId(appWidgetId);
    }

    private WidgetOptions getWidgetOptions(int appWidgetId) {
        Logger.i(TAG, "Loading Widget Options");

        WidgetOptions options = widgetManager.load(appWidgetId);
        if (options == null) {
            Logger.i(TAG, "Can't find Widget Options for id: %s", appWidgetId);
            return new WidgetOptions();
        }
        return options;
    }

    private void saveWidgetOptions() {
        Logger.i(TAG, "Saving Widget Options");

        //TODO

        widgetOptions.widgetId = getAppWidgetId();
        widgetOptions.title = titleEditText.getText().toString();

        Time time = new Time();
        time.year = datePicker.getYear();
        time.month = datePicker.getMonth();
        time.monthDay = datePicker.getDayOfMonth();
        time.hour = timePicker.getCurrentHour();
        time.minute = timePicker.getCurrentMinute();

        widgetOptions.timestamp = time.toMillis(false);

        widgetOptions.upward = true;

        widgetOptions.enableSeconds = secondsCheckBox.isChecked();

        widgetOptions.repeatingPeriod = 0L;

        Logger.i(TAG, "Widget Options: %s", widgetOptions);

        widgetManager.add(widgetOptions);

        Logger.d(TAG, "Finished saving Widget Options");
    }

    private void saveDefaultPreferences() {
        Logger.i(TAG, "Saving Global Options");

        List<Integer> list = Util.toIntegerList(options.savedWidgets);
        list.add(getAppWidgetId());

        options.savedWidgets = Util.toIntArray(list);
        options.enableSeconds = secondsCheckBox.isChecked();

        Logger.i(TAG, "Global Options: %s", options);

        prefManager.saveDefaultPrefs(options);

        Logger.d(TAG, "Finished saving Global Options");
    }

    private DefaultOptions loadDefaultOptions() {
        Logger.i(TAG, "Loading Global Options");

        DefaultOptions options = prefManager.loadDefaultPrefs();

        Logger.i(TAG, "Global Options: %s", options);

        return options;
    }

    private void applyOptions(DefaultOptions options, WidgetOptions widgetOptions) {
        Logger.i(TAG, "Applying Options");

        // Default first
        secondsCheckBox.setChecked(options.enableSeconds);

        // Then basically override
        if (widgetOptions.isValid()) {

            titleEditText.setText(widgetOptions.title);

            Time time = new Time();
            time.set(widgetOptions.timestamp);

            datePicker.updateDate(time.year, time.month, time.monthDay);
            timePicker.setCurrentHour(time.hour);
            timePicker.setCurrentMinute(time.minute);

            secondsCheckBox.setChecked(widgetOptions.enableSeconds);
        }
    }



}