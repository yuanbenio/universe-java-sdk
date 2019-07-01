package com.yuanbenlian.model.entity;

import com.yuanbenlian.common.Constants;

import java.util.Comparator;
import java.util.TreeMap;

public class Article extends TreeMap implements BaseEntity {
    @Override
    public String getType() {
        return Constants.TYPE_ARTICLE;
    }
    @Override
    public void sort(){
        TreeMap<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        map.putAll(this);
        this.clear();
        this.putAll(map);
    }

    @Override
    public TreeMap<String, String> toMap() {
        return this;
    }
}
