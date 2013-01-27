package com.ovio.countdown.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

/**
 * Countdown
 * com.ovio.countdown
 */
public class FixedTabHost extends TabHost {

    public FixedTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedTabHost(Context context) {
        super(context);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        // API LEVEL <7 occasionally throws a NPE here
        if(getCurrentView() != null){
            super.dispatchWindowFocusChanged(hasFocus);
        }

    }

/* TODO
    @Override
    protected void onAttachedToWindow() {

        setup();

        TabHost.TabSpec tabSpec;

        tabSpec = newTabSpec("tag1");

        tabSpec.setIndicator(getContext().getString(R.string.tab_manual));
        tabSpec.setContent(R.id.tabManual);
        addTab(tabSpec);

        tabSpec = newTabSpec("tag2");
        tabSpec.setIndicator(getContext().getString(R.string.tab_google));
        tabSpec.setContent(R.id.tabGoogle);
        addTab(tabSpec);

        super.onAttachedToWindow();
    }
*/
}
