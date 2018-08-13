/*
 * Copyright 2018 Seven Seals Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
