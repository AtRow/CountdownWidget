package com.ovio.countdown.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.util
 */
public class Util {

    public static final String TF = "%Y.%m.%d %H:%M:%S";

    public static String packIntArray(int[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(';');
        }
        return sb.toString();
    }

    public static int[] unpackIntArray(String str) {
        if (str == null || str == "") {
            return new int[0];
        }
        String[] chunks = str.split(";");
        int[] array = new int[chunks.length];

        for (int i = 0; i < chunks.length; i++) {
            array[i] = Integer.parseInt(chunks[i]);
        }
        return array;
    }

    public static int[] toIntArray(List<Integer> list){
        int[] array = new int[list.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static List<Integer> toIntegerList(int[] array) {
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public static String getString(Collection<?> objects) {
        if (objects == null) {
            return "NULL";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        int i = 0;

        for (Object o: objects) {
            sb.append(o);
            if (i < objects.size() - 1) {
                sb.append(", ");
            }
            i++;
        }

        sb.append(']');
        return sb.toString();
    }

    public static String getString(int[] array) {
        if (array == null) {
            return "NULL";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(']');
        return sb.toString();
    }
}
