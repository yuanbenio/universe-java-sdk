package com.yuanben.model.http;

import com.alibaba.fastjson.JSONObject;
import com.yuanben.model.Metadata;

/**
 * <p>查询metadata的响应结果体</p>
 */
public class MetadataQueryResp {

    /**
     * code="error"表示请求失败
     */
    private String code;
    /**
     * 返回的错误信息
     */
    private String msg;
    private Metadata data;
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

    public Metadata getData() {
        return data;
    }

    public void setData(Metadata data) {
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

    public MetadataQueryResp convert(String json) {
        return JSONObject.parseObject(json, MetadataQueryResp.class);
    }
}
