package com.ovio.countdown.prefs;

import android.util.Pair;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public abstract class SpinnerMapping<T> {

    private List<Pair<Integer, T>> list = getList();

    private Pair<Integer, T> defaultPair = getDefault();


    protected abstract Pair<Integer,T> getDefault();

    protected abstract List<Pair<Integer, T>> getList();

    Pair<Integer, T> getPair(int resId, T val) {
        return new Pair<Integer, T>(resId, val);
    }

    public int getTextId(int id) {
        Pair<Integer, T> pair = list.get(id);
        if (pair == null) {
            pair = defaultPair;
        }

        return pair.first;
    }

    public T getValueForId(int id) {
        Pair<Integer, T> pair = list.get(id);
        if (pair == null) {
            pair = defaultPair;
        }

        return pair.second;
    }

    public int getSize() {
        return list.size();
    }

    public int getIdForVal(T value) {
        Pair<Integer, T> pair = null;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).second.equals(value)) {
                return i;
            }
        }
        return 0;
    }
}

