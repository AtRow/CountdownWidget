package com.ovio.countdown;

import java.util.ArrayList;
import java.util.List;

abstract class Data {

    private List<Integer> list = getList();

    protected abstract List<Integer> getList();

    public int getSize() {
        return list.size();
    }
}

public class TestData extends Data {

    private ArrayList<Integer> testList = new ArrayList<Integer>() {
        {
            add(1);
            add(2);
            add(3);
        }
    };

    @Override
    protected List<Integer> getList() {
        return testList;
    }

    public static void main(String ... args) {
        TestData testData = new TestData();

        System.out.println("Size: " + testData.getSize());
    }
}

