package com.yuanbenlian.model.entity;

import com.google.gson.Gson;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.StringUtils;

import java.util.TreeMap;

public class File implements BaseEntity {
    private String ext;
    private String size; //bytes
    private String name;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return Constants.TYPE_FILE;
    }


    @Override
    public int size() {
        int size = 0;
        if (StringUtils.isNotBlank(ext)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.name)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.ext)) {
            size++;
        }
        return size;
    }

    @Override
    public void sort() {

    }

    @Override
    public TreeMap<String, String> toMap() {
        Gson gson = GsonUtil.getInstance();
        return gson.fromJson(gson.toJson(this), TreeMap.class);
    }
}