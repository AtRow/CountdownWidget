package com.ovio.countdown.prefs;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Toast;
import com.ovio.countdown.R;
import com.ovio.countdown.event.CalendarManager;
import com.ovio.countdown.event.EventData;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.GeneralOptions;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.service.WidgetService;
import com.ovio.countdown.util.Util;
import com.ovio.countdown.view.WidgetPreferencesView;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesActivity extends Activity {

    private static final String TAG = Logger.PREFIX + "PrefsActivity";

    private final Context self = this;

    private static final int PICK_DATE_REQUEST = 1;
    private static final int PICK_EVENTS_DATE_REQUEST = 2;
    private static final int PICK_EVENT_REQUEST = 3;
    private static final int PICK_ICON_REQUEST = 4;
    private static final int PICK_STYLE_REQUEST = 5;

    private Integer appWidgetId;

    private PreferencesManager prefManager;

    private WidgetPreferencesManager widgetManager;

    private WidgetOptions widgetOptions;

    private EventData widgetCalendarEvent;

    private GeneralOptions options;

    private boolean doForceRemoveOnCancel;

    private WidgetPreferencesView view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "Created Widget Preferences Activity");

        setContentView(R.layout.preferences);

        view = new WidgetPreferencesView(this);

        setResultCanceled();

        prefManager = PreferencesManager.getInstance(self);
        widgetManager = WidgetPreferencesManager.getInstance(self);

        doForceRemoveOnCancel = true;

        appWidgetId = getAppWidgetId();
        widgetOptions = getWidgetOptions(appWidgetId);

        // Enable force remove widget if it's not stored
        doForceRemoveOnCancel = !widgetOptions.isValid();

        options = loadDefaultOptions();

        applyOptions(options, widgetOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    public void onTimeCheckBoxClick(View view) {
        this.view.onTimeCheckBoxClick(view);
    }

    public void onCalendarPickerClick(View view) {
        Time date = this.view.getTime();
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        DateHelper.putDateToIntent(date, intent);
        startActivityForResult(intent, PICK_DATE_REQUEST);
    }

    public void onCalendarEventPickerClick(View view) {
        Intent intent = new Intent(getApplicationContext(), DayInfoCalendarActivity.class);
        startActivityForResult(intent, PICK_EVENTS_DATE_REQUEST);
    }

    public void onIconButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), IconPickerActivity.class);
        startActivityForResult(intent, PICK_ICON_REQUEST);
    }

    public void onStyleButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), StylePickerActivity.class);
        startActivityForResult(intent, PICK_STYLE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Time date;

            switch (requestCode) {
                case PICK_DATE_REQUEST:
                    date = DateHelper.getDateFromIntent(data);
                    if (date != null) {
                        this.view.setDate(date);
                    }
                    break;

                case PICK_EVENTS_DATE_REQUEST:
                    date = DateHelper.getDateFromIntent(data);
                    if (date != null) {
                        startEventPickerForDate(date);
                    }
                    break;

                case PICK_EVENT_REQUEST:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        widgetCalendarEvent = (EventData) extras.getSerializable(EventPickerActivity.EVENT);
                        if (widgetCalendarEvent != null) {
                            this.view.setEventData(widgetCalendarEvent);
                            this.view.setCalendar(CalendarManager.NONE_CALENDARS);
                        }
                    }
                    break;

                case PICK_ICON_REQUEST:
                    int iconId = data.getIntExtra(IconPickerActivity.ID, IconMapping.NONE);
                    this.view.setIcon(iconId);

                    break;

                case PICK_STYLE_REQUEST:
                    int styleId = data.getIntExtra(StylePickerActivity.ID, StyleMapping.DEFAULT);
                    this.view.setStyle(styleId);

                    break;
            }
        }
    }

    private void startEventPickerForDate(Time date) {
        Intent intent = new Intent(getApplicationContext(), EventPickerActivity.class);
        DateHelper.putDateToIntent(date, intent);
        startActivityForResult(intent, PICK_EVENT_REQUEST);
    }

    private void sendToWidgetService() {
        Logger.i(TAG, "Sending WIDGET_UPDATED intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.WIDGET_UPDATED);

        Bundle extras = widgetOptions.getBundle();

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

        widgetOptions = new WidgetOptions();

        widgetOptions.widgetId = getAppWidgetId();

        if (view.isConfigManual()) {

            widgetOptions.title = view.getTitle();

            Time time = view.getTime();
            widgetOptions.timestamp = time.toMillis(false);

            widgetOptions.enableTime = view.getEnableTime();
            widgetOptions.recurringInterval = view.getRecurringInterval();
            widgetOptions.notificationInterval = view.getNotificationInterval();

        } else {

            widgetOptions.calendarId = view.getCalendar();
            widgetOptions.concreteEvent = (widgetOptions.calendarId == CalendarManager.NONE_CALENDARS);

            EventData eventData = view.getEventData();
            if (eventData != null) {
                widgetOptions.eventId = eventData.eventId;
                widgetOptions.timestamp = eventData.start;
                widgetOptions.instanceId = eventData.id;
            }

        }

        widgetOptions.enableSeconds = view.getCountSeconds();
        widgetOptions.countUp = view.getCountUp();
        widgetOptions.icon = view.getIcon();
        widgetOptions.style = view.getStyle();

        Logger.i(TAG, "Widget Options: %s", widgetOptions);

        widgetManager.save(widgetOptions);

        Logger.d(TAG, "Finished saving Widget Options");
    }

    private void saveDefaultPreferences() {
        Logger.i(TAG, "Saving Global Options");

        List<Integer> list = Util.toIntegerList(options.savedWidgets);
        list.add(getAppWidgetId());

        options.savedWidgets = Util.toIntArray(list);

        options.enableSeconds = view.getCountSeconds();
        options.enableTime = view.getEnableTime();


        Logger.i(TAG, "Global Options: %s", options);

        prefManager.saveDefaultPrefs(options);

        Logger.d(TAG, "Finished saving Global Options");
    }

    private GeneralOptions loadDefaultOptions() {
        Logger.i(TAG, "Loading Global Options");

        GeneralOptions options = prefManager.loadDefaultPrefs();

        Logger.i(TAG, "Global Options: %s", options);

        return options;
    }

    private void applyOptions(GeneralOptions options, WidgetOptions widgetOptions) {
        Logger.i(TAG, "Applying Options");

        // Default first
        view.setCountSeconds(options.enableSeconds);
        view.setCountUp(options.countUp);
        view.setEnableTime(options.enableTime);

        // Then basically override
        if (widgetOptions.isValid()) {

            if (widgetOptions.eventId == 0) {

                view.setTitle(widgetOptions.title);

                Time time = new Time();
                time.set(widgetOptions.timestamp);

                view.setDateTime(time);

                view.setEnableTime(widgetOptions.enableTime);

                view.setRecurringInterval(widgetOptions.recurringInterval);

                view.setNotificationInterval(widgetOptions.notificationInterval);

            } else {

                if (widgetOptions.concreteEvent) {
                    CalendarManager calendarManager = CalendarManager.getInstance(getApplicationContext());
                    EventData eventData = calendarManager.getEvent(widgetOptions.timestamp, widgetOptions.eventId);
                    view.setEventData(eventData);

                } else {
                    view.setCalendar(widgetOptions.calendarId);
                }
            }

            view.setCountSeconds(widgetOptions.enableSeconds);

            view.setCountUp(widgetOptions.countUp);

            view.setIcon(widgetOptions.icon);

            view.setStyle(widgetOptions.style);
        }
    }

}