package com.yuanben.model.http;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * <p>license查询结果封装</p>
 */
public class LicenseQueryResp {
    /**
     * code="error"表示请求失败
     */
    private String code;
    /**
     * 返回的错误信息
     */
    private String msg;
    private Map<String, Object> data;
    private Transaction tx;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Transaction getTx() {
        return tx;
    }

    public void setTx(Transaction tx) {
        this.tx = tx;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    public LicenseQueryResp convert(String json) {
        return JSONObject.parseObject(json, LicenseQueryResp.class);
    }
}
