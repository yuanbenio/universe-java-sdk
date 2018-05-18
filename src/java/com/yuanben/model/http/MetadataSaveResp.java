package com.yuanben.model.http;

import com.yuanben.util.GsonUtil;

/**
 * <p>向node节点注册metadata的返回结果体</p>
 */
public class MetadataSaveResp {

    public class Data {
        private String dna;

        public String getDna() {
            return dna;
        }

        public void setDna(String dna) {
            this.dna = dna;
        }
    }

    /**
     * code="error"表示请求失败
     */
    private String code;
    /**
     * 返回的错误信息
     */
    private String msg;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String toJson() {
        return GsonUtil.getInstance().toJson(this);
    }

    public MetadataSaveResp convert(String json) {
        return GsonUtil.getInstance().fromJson(json, MetadataSaveResp.class);
    }
}
