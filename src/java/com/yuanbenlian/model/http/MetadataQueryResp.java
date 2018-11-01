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

package com.yuanbenlian.model.http;

import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.util.GsonUtil;

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
     * error message
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
        return GsonUtil.getInstance().toJson(this);
    }

    public MetadataQueryResp convert(String json) {
        return GsonUtil.getInstance().fromJson(json, MetadataQueryResp.class);
    }
}
