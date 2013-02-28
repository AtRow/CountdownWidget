package com.ovio.countdown.view;

import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.ovio.countdown.R;
import com.ovio.countdown.event.CalendarData;
import com.ovio.countdown.event.CalendarManager;
import com.ovio.countdown.event.EventData;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.prefs.*;

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

    private final WidgetPreferencesActivity activity;

    private CalendarManager calendarManager;

    private boolean isCalendarCompatible;

    private boolean isManualTab = true;

    RecurringSpinnerMapping recurringSpinnerMapping = new RecurringSpinnerMapping();

    NotificationSpinnerMapping notificationSpinnerMapping = new NotificationSpinnerMapping();

    private int currentRecurringId;

    private int currentNotificationId;

    private int currentIconId;

    private int currentStyleId;

    private long currentCalendarId = CalendarManager.NONE_CALENDARS;

    boolean ignoringFirstAssignment = true;


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

    private Spinner notificationSpinner;

    private ImageView iconImageView;

    private ImageView iconImageViewG;

    private ImageView styleImageView;

    private ImageView styleImageViewG;

    private Button iconButton;

    private Button iconButtonG;

    private Button styleButton;

    private Button styleButtonG;

    private Spinner calendarSpinner;

    private TextView calendarInfoTextView;

    private LinearLayout calendarControlsLayout;

    private EventData eventData;

    public WidgetPreferencesView(WidgetPreferencesActivity activity) {
        this.activity = activity;

        obtainFormElements();

        initTabHost();
        initCalendarPickers();
        initRecurringSpinner();
        initNotificationSpinner();

        updateOkButton();
    }

    private void initNotificationSpinner() {

        int size = notificationSpinnerMapping.getSize();
        String[] data = new String[size];

        for (int i = 0; i < size; i++) {
            data[i] = activity.getString(notificationSpinnerMapping.getTextId(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(adapter);

        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                currentNotificationId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentNotificationId = 0;
            }
        });

    }

    private void initRecurringSpinner() {

        int size = recurringSpinnerMapping.getSize();
        String[] data = new String[size];

        for (int i = 0; i < size; i++) {
            data[i] = activity.getString(recurringSpinnerMapping.getTextId(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(adapter);

        recurringSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                currentRecurringId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentRecurringId = 0;
            }
        });

    }

    private void initCalendarPickers() {
        calendarManager = CalendarManager.getInstance(activity);

        if (calendarManager.isCompatible()) {
            isCalendarCompatible = true;

        } else {
            Logger.i(TAG, "Device is NOT CalendarData-compatible; disabling controls");

            isCalendarCompatible = false;
            setGoogleCalendarControlsEnabled(isCalendarCompatible);
            updateOkButton();
            return;
        }

        setGoogleCalendarControlsEnabled(isCalendarCompatible);

        List<CalendarData> calendarDatas = calendarManager.getCalendars();

        final ArrayAdapter calendarAdapter = new ArrayAdapter<CalendarData>(activity, android.R.layout.simple_spinner_item, calendarDatas);
        calendarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        calendarSpinner.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        countUpCheckBoxG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                reloadCalendarEvent();
            }
        });

        ignoringFirstAssignment = true;

        calendarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                EventData calendarEventData = null;

                if (ignoringFirstAssignment) {
                    ignoringFirstAssignment = false;

                } else {

                    CalendarData calendarData = (CalendarData) calendarAdapter.getItem(position);
                    currentCalendarId = calendarData.id;

                    long now = System.currentTimeMillis();
                    if (getCountUp()) {
                        calendarEventData = calendarManager.getPreviousCalendarEvent(currentCalendarId, now);
                    } else {
                        calendarEventData = calendarManager.getNextCalendarEvent(currentCalendarId, now);
                    }

                    if (calendarEventData == null) {
                        Toast.makeText(activity, R.string.event_picker_no_events_found, Toast.LENGTH_LONG).show();
                    }

                }
                setEventData(calendarEventData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

    }

    private void reloadCalendarEvent() {

        if (currentCalendarId != CalendarManager.NONE_CALENDARS) {

            List<CalendarData> calendars = calendarManager.getCalendars();

            for (int i = 0; i < calendars.size(); i++) {
                if (calendars.get(i).id == currentCalendarId) {
                    calendarSpinner.setSelection(i);
                    calendarSpinner.getOnItemSelectedListener().onItemSelected(null, null, i, 0L);
                }
            }
        }
    }

    public void setEventData(EventData eventData) {

        if (isManualTab) {
            tabHost.setCurrentTabByTag(TAB_GOOGLE);
        }

        if (eventData != null) {
            this.eventData = eventData;
            // TODO Human Text
            calendarInfoTextView.setText(eventData.toString());
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

    public void setTime(Time time) {
        timePicker.setCurrentHour(time.hour);
        timePicker.setCurrentMinute(time.minute);
    }

    public void setDate(Time time) {
        datePicker.updateDate(time.year, time.month, time.monthDay);
    }

    public void setDateTime(Time time) {
        setDate(time);
        setTime(time);
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

        if (isManualTab) {
            return secondsCheckBox.isChecked();
        } else {
            return secondsCheckBoxG.isChecked();
        }
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

        if (isManualTab) {
            return countUpCheckBox.isChecked();
        } else {
            return countUpCheckBoxG.isChecked();
        }
    }

    public void setRecurringInterval(long interval) {
        currentRecurringId = recurringSpinnerMapping.getIdForVal(interval);
        recurringSpinner.setSelection(currentRecurringId);
    }

    public long getRecurringInterval() {
        return recurringSpinnerMapping.getValueForId(currentRecurringId);
    }

    public void setNotificationInterval(long interval) {
        currentNotificationId = notificationSpinnerMapping.getIdForVal(interval);
        notificationSpinner.setSelection(currentNotificationId);
    }

    public long getNotificationInterval() {
        return notificationSpinnerMapping.getValueForId(currentNotificationId);
    }

    public void onTimeCheckBoxClick(View view) {

        if (enableTimeCheckBox.isChecked()) {
            timePicker.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.GONE);
        }
    }


    public EventData getEventData() {
        return eventData;
    }

    public int getIcon() {
        return currentIconId;
    }

    public void setIcon(int iconId) {
        currentIconId = iconId;

        if (iconId != IconMapping.NONE) {
            int resId = IconMapping.getInstance().getResource(iconId);

            iconImageView.setImageResource(resId);
            iconImageViewG.setImageResource(resId);

        } else {
            iconImageView.setImageDrawable(null);
            iconImageViewG.setImageDrawable(null);
        }
    }

    public int getStyle() {
        return currentStyleId;
    }

    public void setStyle(int styleId) {
        currentStyleId = styleId;

        int resId = StyleMapping.getInstance().getResource(styleId);

        styleImageView.setBackgroundResource(resId);
        styleImageViewG.setBackgroundResource(resId);
    }

    public long getCalendar() {
        return currentCalendarId;
    }

    public void setCalendar(long calendarId) {
        currentCalendarId = calendarId;
        reloadCalendarEvent();
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

        notificationSpinner = (Spinner) activity.findViewById(R.id.notifySpinner);

        iconImageView = (ImageView) activity.findViewById(R.id.iconImageView);

        iconImageViewG = (ImageView) activity.findViewById(R.id.iconImageViewG);

        styleImageView = (ImageView) activity.findViewById(R.id.styleImageView);

        styleImageViewG = (ImageView) activity.findViewById(R.id.styleImageViewG);

        iconButton = (Button) activity.findViewById(R.id.iconButton);

        iconButtonG = (Button) activity.findViewById(R.id.iconButtonG);

        styleButton = (Button) activity.findViewById(R.id.styleButton);

        styleButtonG = (Button) activity.findViewById(R.id.styleButtonG);

        calendarSpinner = (Spinner) activity.findViewById(R.id.calendarSpinner);

        calendarInfoTextView = (TextView) activity.findViewById(R.id.calendarErrorTextView);

        calendarControlsLayout = (LinearLayout) activity.findViewById(R.id.calendarControlsLayout);

        okButton = (Button) activity.findViewById(R.id.okButton);

        cancelButton = (Button) activity.findViewById(R.id.cancelButton);

    }

    public boolean isConfigManual() {
        return isManualTab;
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
                (eventData != null));

        okButton.setEnabled(enabled);
    }

    private void onManualTab() {
        secondsCheckBox.setChecked(secondsCheckBoxG.isChecked());
        countUpCheckBox.setChecked(countUpCheckBoxG.isChecked());

        // TODO: check
        iconImageView.setImageDrawable(iconImageViewG.getDrawable());
        styleImageView.setImageDrawable(styleImageViewG.getDrawable());

        isManualTab = true;

        updateOkButton();
    }

    private void onGoogleTab() {
        secondsCheckBoxG.setChecked(secondsCheckBox.isChecked());
        countUpCheckBoxG.setChecked(countUpCheckBox.isChecked());

        // TODO: check
        iconImageViewG.setImageDrawable(iconImageView.getDrawable());
        styleImageViewG.setImageDrawable(styleImageView.getDrawable());

        isManualTab = false;

        updateOkButton();
    }

}