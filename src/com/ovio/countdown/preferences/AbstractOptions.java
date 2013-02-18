package com.ovio.countdown.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.ovio.countdown.util.Util;

import java.lang.reflect.Array;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public abstract class AbstractOptions {

    // TODO: a perfect case to use Annotations

    public static class Field {

        public final Class<?> clazz;
        public final String name;

        private Field(String name, Class<?> clazz) {
            this.clazz = clazz;
            this.name = name;
        }

        public static Field get(String name, Class<?> clazz) {
            return new Field(name, clazz);
        }
    }

    protected Bundle bundle = new Bundle();
    private final Field[] fields;


    public AbstractOptions(Field[] fields) {
        this.fields = fields;
    }

    public void readFromPrefs(SharedPreferences preferences) {
        bundle.clear();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.name;

            if (field.clazz.equals(Boolean.class)) {
                bundle.putBoolean(fieldName, preferences.getBoolean(fieldName, false));

            } else if (field.clazz.equals(Integer.class)) {
                bundle.putInt(fieldName, preferences.getInt(fieldName, 0));

            } else if (field.clazz.equals(Long.class)) {
                bundle.putLong(fieldName, preferences.getLong(fieldName, 0L));

            } else if (field.clazz.equals(String.class)) {
                bundle.putString(fieldName, preferences.getString(fieldName, null));

            } else if (field.clazz.equals(Array.class)) {
                int[] array = Util.unpackIntArray(preferences.getString(fieldName, null));
                bundle.putIntArray(fieldName, array);

            } else {
                throw new IllegalArgumentException("Don't know how to read class: " + field.clazz.getCanonicalName());
            }
        }
        // Write bundle values to fields
        updateFields();
    }

    public void writeToPrefs(SharedPreferences preferences) {
        // Write field values to bundle
        updateBundle();

        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.name;

            if (field.clazz.equals(Boolean.class)) {
                editor.putBoolean(fieldName, bundle.getBoolean(field.name));

            } else if (field.clazz.equals(Integer.class)) {
                editor.putInt(fieldName, bundle.getInt(field.name));

            } else if (field.clazz.equals(Long.class)) {
                editor.putLong(fieldName, bundle.getLong(field.name));

            } else if (field.clazz.equals(String.class)) {
                editor.putString(fieldName, bundle.getString(field.name));

            } else if (field.clazz.equals(Array.class)) {
                String value = Util.packIntArray(bundle.getIntArray(field.name));
                editor.putString(fieldName, value);

            } else {
                throw new IllegalArgumentException("Don't know how to write class: " + field.clazz.getCanonicalName());
            }
        }
        editor.commit();
    }

    public Bundle getBundle() {
        updateBundle();
        return new Bundle(bundle);
    }

    public void setBundle(Bundle bundle) {
        this.bundle = new Bundle(bundle);
        updateFields();
    }

    protected abstract void updateFields();

    protected abstract void updateBundle();

}
