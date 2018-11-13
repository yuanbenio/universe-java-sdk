package com.yuanbenlian.model.entity;

import com.google.gson.Gson;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.StringUtils;

import java.util.TreeMap;

public class Audio implements BaseEntity {

    private String thumb;
    private String original;
    private String ext;
    private String size;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Audio() {
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

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

    public Audio(String thumb, String original, String ext, String duration, String size) {

        this.thumb = thumb;
        this.original = original;
        this.ext = ext;
        this.size = size;
        this.duration = duration;
    }

    @Override
    public String GetType() {
        return Constants.TYPE_AUDIO;
    }

    @Override
    public int size() {
        int size = 0;
        if (StringUtils.isNotBlank(thumb)) {
            size++;
        }
        if (StringUtils.isNotBlank(original)) {
            size++;
        }
        if (StringUtils.isNotBlank(ext)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.duration)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.size)) {
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
