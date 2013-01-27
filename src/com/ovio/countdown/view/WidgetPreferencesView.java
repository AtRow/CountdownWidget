package com.ovio.countdown.view;

import android.app.Activity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;

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



    private TabHost tabHost;

    private EditText titleEditText;

    private DatePicker datePicker;

    private TimePicker timePicker;

    private CheckBox enableTimeCheckBox;

    private CheckBox secondsCheckBox;

    private CheckBox secondsCheckBoxG;

    private CheckBox countUpCheckBox;

    private CheckBox countUpCheckBoxG;

    private ExpandableListView recurringExpandableListView;

    private ExpandableListView notifyExpandableListView;

    private ExpandableListView iconExpandableListView;

    private ExpandableListView iconExpandableListViewG;

    private ExpandableListView styleExpandableListView;

    private ExpandableListView styleExpandableListViewG;

    private Button eventButton;


    public WidgetPreferencesView(Activity activity) {
        this.activity = activity;

        obtainFormElements();
        initTabHost();
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

        recurringExpandableListView = (ExpandableListView) activity.findViewById(R.id.recurringExpandableListView);

        notifyExpandableListView = (ExpandableListView) activity.findViewById(R.id.notifyExpandableListView);

        iconExpandableListView = (ExpandableListView) activity.findViewById(R.id.iconExpandableListView);

        iconExpandableListViewG = (ExpandableListView) activity.findViewById(R.id.iconExpandableListViewG);

        styleExpandableListView = (ExpandableListView) activity.findViewById(R.id.styleExpandableListView);

        styleExpandableListViewG = (ExpandableListView) activity.findViewById(R.id.styleExpandableListViewG);

        eventButton = (Button) activity.findViewById(R.id.eventButton);

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

    private void onManualTab() {
        secondsCheckBox.setChecked(secondsCheckBoxG.isChecked());
        countUpCheckBox.setChecked(countUpCheckBoxG.isChecked());

        iconExpandableListView.setSelection(iconExpandableListViewG.getSelectedItemPosition());
        styleExpandableListView.setSelection(styleExpandableListViewG.getSelectedItemPosition());

    }

    private void onGoogleTab() {
        secondsCheckBoxG.setChecked(secondsCheckBox.isChecked());
        countUpCheckBoxG.setChecked(countUpCheckBox.isChecked());

        iconExpandableListViewG.setSelection(iconExpandableListView.getSelectedItemPosition());
        styleExpandableListViewG.setSelection(styleExpandableListView.getSelectedItemPosition());

    }

}