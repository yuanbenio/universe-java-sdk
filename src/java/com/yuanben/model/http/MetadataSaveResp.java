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
