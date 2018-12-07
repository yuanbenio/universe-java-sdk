package com.yuanbenlian.model.entity;

import java.util.TreeMap;

public interface BaseEntity {
    String GetType();

    int size();

    void sort();

    TreeMap<String,String> toMap();
}
