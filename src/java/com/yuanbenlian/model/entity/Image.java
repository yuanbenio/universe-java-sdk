package com.yuanbenlian.model.entity;

import com.google.gson.Gson;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.StringUtils;

import java.util.TreeMap;

public class Image implements BaseEntity {

    private String thumb;
    private String original;
    private String ext;
    private String width;
    private String height;
    private String size; //bytes

    public Image() {
    }

    public Image(String thumb, String original, String ext, String width, String height, String size) {
        this.thumb = thumb;
        this.original = original;
        this.ext = ext;
        this.width = width;
        this.height = height;
        this.size = size;
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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getType() {
        return Constants.TYPE_IMAGE;
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
        if (StringUtils.isNotBlank(this.size)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.height)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.width)) {
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
