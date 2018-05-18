package com.yuanben.model.http;

import com.google.gson.annotations.SerializedName;
import com.yuanben.util.GsonUtil;

/**
 * <p>注册公钥请求体</p>
 */
public class RegisterAccountReq {

    @SerializedName(value = "signature")
    private String signature;
    @SerializedName(value = "pubkey")
    private String pubKey;
    @SerializedName(value = "subkeys")
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
        return GsonUtil.getInstance().toJson(this);
    }
}
