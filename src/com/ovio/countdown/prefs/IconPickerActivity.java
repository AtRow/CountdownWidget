package com.ovio.countdown.prefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.util.Util;
import android.view.ViewGroup.LayoutParams;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class IconPickerActivity extends Activity {

    public static final String ID = "id";

    private static final String TAG = Logger.PREFIX + "IconPicker";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "Created Icon Picker Activity");

        setContentView(R.layout.image_grid);

        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        GridView gridView = (GridView) findViewById(R.id.gridView);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(listener);

        adapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent();
            Bundle extras = new Bundle();
            extras.putInt(ID, position);
            intent.putExtras(extras);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private BaseAdapter adapter = new BaseAdapter() {

        private IconMapping data = IconMapping.getInstance();

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.getResource(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) { // if it's not recycled, initialize some attributes
                view = new ImageView(getApplicationContext());
            }
            ImageView imageView = (ImageView)view;

            imageView.setImageResource(data.getResource(position));
            imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));

            int pd = Util.toPx(getApplicationContext(), 10);
            imageView.setPadding(pd, pd, pd, pd);

            return view;
        }
    };

}