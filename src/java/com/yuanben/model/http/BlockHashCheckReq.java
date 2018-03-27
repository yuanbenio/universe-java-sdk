package com.yuanben.model.http;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>检查blockHash的请求封装</p>
 */
public class BlockHashCheckReq {

    private String hash;
    private Long height;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
