package com.rapid.framework.logging.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstantPool {
    private Map<String, Integer> stringPool = new HashMap<>();
    private List<String> stringList = new ArrayList<>();

    public String at(int index) {
        return stringList.get(index);
    }

    public boolean hasConstant(String text) {
        return stringPool.containsKey(text);
    }

    public void addConstant(String text) {
        if (stringPool.containsKey(text)) {
            return;
        }
        stringPool.put(text, stringList.size());
        stringList.add(text);
    }

    public int getConstant(String text) {
        if (text == null) {
            return -1;
        }
        Integer i = stringPool.get(text);
        if (i == null) {
            return -1;
        }
        return i;
    }

    public boolean isEmpty() {
        return stringPool.isEmpty();
    }

    public int size() {
        return stringPool.size();
    }

    public Set<String> keys() {
        return stringPool.keySet();
    }

    public void merge(ConstantPool pool) {
        for (String text : pool.keys()) {
            addConstant(text);
        }
    }
}