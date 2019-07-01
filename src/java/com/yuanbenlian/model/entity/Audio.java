package com.yuanbenlian.model.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.yuanbenlian.common.Constants;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.StringUtils;

import java.util.TreeMap;

public class Audio implements BaseEntity {

    private String ext;
    private String size; //bytes
    private String duration;
    @SerializedName("simple_rate")
    private String simpleRate;
    @SerializedName("bit_rate")
    private String bitRate;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Audio() {
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

    public String getSimpleRate() {
        return simpleRate;
    }

    public void setSimpleRate(String simpleRate) {
        this.simpleRate = simpleRate;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public Audio(String ext, String size, String duration, String simpleRate, String bitRate) {

        this.ext = ext;
        this.size = size;
        this.duration = duration;
        this.simpleRate = simpleRate;
        this.bitRate = bitRate;
    }

    @Override
    public String getType() {
        return Constants.TYPE_AUDIO;
    }

    @Override
    public int size() {
        int size = 0;
        if (StringUtils.isNotBlank(this.simpleRate)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.bitRate)) {
            size++;
        }
        if (StringUtils.isNotBlank(this.ext)) {
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
