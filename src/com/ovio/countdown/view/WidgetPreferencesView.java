package com.ovio.countdown.view;

import android.app.Activity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.ovio.countdown.R;
import com.ovio.countdown.event.Calendar;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.event.EventManager;
import com.ovio.countdown.log.Logger;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesView {

    private static final String TAG = Logger.PREFIX + "PrefsView";

    private static final String TAB_MANUAL = "TAB_MANUAL";

    private static final String TAB_GOOGLE = "TAB_GOOGLE";

    private static final int DEFAULT_DAY_BEGIN_HOUR = 9;

    private final Activity activity;

    private EventManager eventManager;

    private boolean isCalendarCompatible;

    private boolean isManualTab = true;

    private Button okButton;

    private Button cancelButton;

    private TabHost tabHost;

    private EditText titleEditText;

    private DatePicker datePicker;

    private TimePicker timePicker;

    private CheckBox enableTimeCheckBox;

    private CheckBox secondsCheckBox;

    private CheckBox secondsCheckBoxG;

    private CheckBox countUpCheckBox;

    private CheckBox countUpCheckBoxG;

    private Spinner recurringSpinner;

    private Spinner notifySpinner;

    private Spinner iconSpinner;

    private Spinner iconSpinnerG;

    private Spinner styleSpinner;

    private Spinner styleSpinnerG;

    private Spinner calendarSpinner;

    private TextView calendarInfoTextView;

    private LinearLayout calendarControlsLayout;

    private Event widgetEvent;


    public WidgetPreferencesView(Activity activity) {
        this.activity = activity;

        obtainFormElements();

        initTabHost();
        initCalendarPickers();

        updateOkButton();
    }

    private void initCalendarPickers() {
        eventManager = EventManager.getInstance(activity);

        if (eventManager.isCompatible()) {
            isCalendarCompatible = true;

        } else {
            Logger.i(TAG, "Device is NOT Calendar-compatible; disabling controls");

            isCalendarCompatible = false;
            setGoogleCalendarControlsEnabled(isCalendarCompatible);
            updateOkButton();
            return;
        }

        setGoogleCalendarControlsEnabled(isCalendarCompatible);

        List<Calendar> calendars = eventManager.getCalendars();

        ArrayAdapter calendarAdapter = new ArrayAdapter<Calendar>(activity, android.R.layout.simple_spinner_item, calendars);
        calendarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        calendarSpinner.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        Time time = new Time();
        time.setToNow();
        time.month--;
        long start = time.toMillis(false);

        time.month += 2;
        long end = time.toMillis(false);

        final List<Event> events = eventManager.getEvents(start, end);

        final ArrayAdapter eventAdapter = new ArrayAdapter<Event>(activity, android.R.layout.simple_spinner_item, events);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        eventSpinner.setAdapter(eventAdapter);
//        eventAdapter.notifyDataSetChanged();

    }

    public void setEvent(Event widgetEvent) {
        if (widgetEvent != null) {
            this.widgetEvent = widgetEvent;
            // TODO
            calendarInfoTextView.setText(widgetEvent.toString());
        } else {
            calendarInfoTextView.setText(R.string.calendar_not_choosen_text);
        }
        updateOkButton();
    }

    private void setGoogleCalendarControlsEnabled(boolean enabled) {

        if (enabled) {
            calendarInfoTextView.setText(R.string.calendar_not_choosen_text);
            calendarControlsLayout.setVisibility(View.VISIBLE);
            Logger.i(TAG, "Setting visibility to View.VISIBLE");

        } else {
            calendarInfoTextView.setText(R.string.calendar_error_text);
            calendarControlsLayout.setVisibility(View.GONE);
            Logger.i(TAG, "Setting visibility to View.GONE");
        }
    }

    public void setTitle(String text) {
        titleEditText.setText(text);
    }

    public String getTitle() {
        return titleEditText.getText().toString();
    }
/*

    public void setDate(Time time) {
        datePicker.updateDate(time.year, time.month, time.monthDay);
    }

    public Time getDate() {
        Time time = new Time();
        time.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        return time;
    }

*/
    public void setTime(Time time) {
        timePicker.setCurrentHour(time.hour);
        timePicker.setCurrentMinute(time.minute);

        datePicker.updateDate(time.year, time.month, time.monthDay);
    }

    public Time getTime() {
        Time time = new Time();

        if (getEnableTime()) {
            time.hour = timePicker.getCurrentHour();
            time.minute = timePicker.getCurrentMinute();
        } else {
            time.hour = DEFAULT_DAY_BEGIN_HOUR;
            time.minute = 0;
        }

        time.year = datePicker.getYear();
        time.month = datePicker.getMonth();
        time.monthDay = datePicker.getDayOfMonth();
        return time;
    }

    public void setCountSeconds(boolean doCount) {
        secondsCheckBox.setChecked(doCount);
        secondsCheckBoxG.setChecked(doCount);
    }

    public boolean getCountSeconds() {
        boolean checked = secondsCheckBox.isChecked();
        boolean checkedG = secondsCheckBoxG.isChecked();

        if ((checked && checkedG) != (checked || checkedG)) {
            // TODO
            String msg = "secondsCheckBox UnSync Error";
            Toast.makeText(activity, msg, Toast.LENGTH_LONG);
            Logger.e(TAG, msg);
        }

        return checked;
    }

    public void setEnableTime(boolean doSet) {
        enableTimeCheckBox.setChecked(doSet);
        onTimeCheckBoxClick(null);
    }

    public boolean getEnableTime() {
        return enableTimeCheckBox.isChecked();
    }

    public void setCountUp(boolean doCount) {
        countUpCheckBox.setChecked(doCount);
        countUpCheckBoxG.setChecked(doCount);
    }

    public boolean getCountUp() {

        boolean checked = countUpCheckBox.isChecked();
        boolean checkedG = countUpCheckBoxG.isChecked();

        if ((checked && checkedG) != (checked || checkedG)) {
          // TODO
            String msg = "countUpCheckBox UnSync Error";
            Toast.makeText(activity, msg, Toast.LENGTH_LONG);
            Logger.e(TAG, msg);
        }

        return checked;
    }

    public void onTimeCheckBoxClick(View view) {

        if (enableTimeCheckBox.isChecked()) {
            timePicker.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.GONE);
        }
    }

    private void initTabHost() {

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec(TAB_MANUAL);


        tabSpec.setIndicator(activity.getString(R.string.tab_manual));
        tabSpec.setContent(R.id.tabManual);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_GOOGLE);
        tabSpec.setIndicator(activity.getString(R.string.tab_google));
        tabSpec.setContent(R.id.tabGoogle);
        tabHost.addTab(tabSpec);

        isManualTab = true;

        //tabHost.setCurrentTabByTag("tag1");

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabListener());
    }

    private void obtainFormElements() {

        tabHost = (TabHost) activity.findViewById(R.id.defaultTabHost);

        titleEditText = (EditText) activity.findViewById(R.id.titleEditText);

        datePicker = (DatePicker) activity.findViewById(R.id.datePicker);

        timePicker = (TimePicker) activity.findViewById(R.id.timePicker);

        timePicker.setIs24HourView( DateFormat.is24HourFormat(activity) );

        enableTimeCheckBox = (CheckBox) activity.findViewById(R.id.enableTimeCheckBox);

        secondsCheckBox = (CheckBox) activity.findViewById(R.id.secondsCheckBox);

        secondsCheckBoxG = (CheckBox) activity.findViewById(R.id.secondsCheckBoxG);

        countUpCheckBox = (CheckBox) activity.findViewById(R.id.countUpCheckBox);

        countUpCheckBoxG = (CheckBox) activity.findViewById(R.id.countUpCheckBoxG);

        recurringSpinner = (Spinner) activity.findViewById(R.id.recurringSpinner);

        notifySpinner = (Spinner) activity.findViewById(R.id.notifySpinner);

        iconSpinner = (Spinner) activity.findViewById(R.id.iconSpinner);

        iconSpinnerG = (Spinner) activity.findViewById(R.id.iconSpinnerG);

        styleSpinner = (Spinner) activity.findViewById(R.id.styleSpinner);

        styleSpinnerG = (Spinner) activity.findViewById(R.id.styleSpinnerG);

        calendarSpinner = (Spinner) activity.findViewById(R.id.calendarSpinner);

        calendarInfoTextView = (TextView) activity.findViewById(R.id.calendarErrorTextView);

        calendarControlsLayout = (LinearLayout) activity.findViewById(R.id.calendarControlsLayout);

        okButton = (Button) activity.findViewById(R.id.okButton);

        cancelButton = (Button) activity.findViewById(R.id.cancelButton);

    }

    protected class TabListener implements TabHost.OnTabChangeListener {

        @Override
        public void onTabChanged(String s) {
            if (s.equals(TAB_GOOGLE)) {
                onGoogleTab();

            } else if (s.equals(TAB_MANUAL)) {
                onManualTab();
            }
        }
    }

    private void updateOkButton() {
        boolean enabled = isManualTab ||
                (isCalendarCompatible &&
                (widgetEvent != null));

        okButton.setEnabled(enabled);
    }

    private void onManualTab() {
        secondsCheckBox.setChecked(secondsCheckBoxG.isChecked());
        countUpCheckBox.setChecked(countUpCheckBoxG.isChecked());

        iconSpinner.setSelection(iconSpinnerG.getSelectedItemPosition());
        styleSpinner.setSelection(styleSpinnerG.getSelectedItemPosition());

        isManualTab = true;

        updateOkButton();
    }

    private void onGoogleTab() {
        secondsCheckBoxG.setChecked(secondsCheckBox.isChecked());
        countUpCheckBoxG.setChecked(countUpCheckBox.isChecked());

        iconSpinnerG.setSelection(iconSpinner.getSelectedItemPosition());
        styleSpinnerG.setSelection(styleSpinner.getSelectedItemPosition());

        isManualTab = false;

        updateOkButton();
    }

}