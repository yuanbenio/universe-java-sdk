package com.yuanben.model.http;

import com.yuanben.util.GsonUtil;

/**
 * <p>检查blcokHash是否在链上的结果封装</p>
 */
public class BlockHashCheckResp {

    private String code;
    private String msg;
    private Boolean data;

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

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }

    public String toJson() {
        return GsonUtil.getInstance().toJson(this);
    }
}
