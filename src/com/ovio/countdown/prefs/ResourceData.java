package com.ovio.countdown.prefs;

import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class ResourceData {

    private TreeMap<Integer, Integer> idToResMap = new TreeMap<Integer, Integer>();
    private TreeMap<Integer, Integer> resToIdMap = new TreeMap<Integer, Integer>();

    public int getId(int resourceId) {
        return resToIdMap.get(resourceId);
    }

    public int getResource(int id) {
        return idToResMap.get(id);
    }

    void put(int id, int resourceId) {
        idToResMap.put(id, resourceId);
        resToIdMap.put(resourceId, id);
    }

    public int size() {
        return idToResMap.size();
    }
}
