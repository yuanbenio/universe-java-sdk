package com.yuanben.model.http;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * <p>注册公钥请求体</p>
 */
public class RegisterAccountReq {

    @JSONField(name = "signature")
    private String signature;
    @JSONField(name = "pubkey")
    private String pubKey;
    @JSONField(name = "subkeys")
    private String[] subPubKeys;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String[] getSubPubKeys() {
        return subPubKeys;
    }

    public void setSubPubKeys(String[] subPubKeys) {
        this.subPubKeys = subPubKeys;
    }
    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
