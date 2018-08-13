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

package com.yuanben.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.yuanben.common.Constants;
import com.yuanben.util.GsonUtil;
import com.yuanben.util.MapUtil;

import java.util.Set;
import java.util.TreeMap;

/**
 * <p>dtcp协议抽象模型</p>
 */
public class Metadata {
    @SerializedName("pubkey")
    private String pubKey;
    @SerializedName( "block_hash")
    private String blockHash;
    @SerializedName( "block_height")
    private String blockHeight;
    private String signature;
    @SerializedName( "id")
    private String id;


    //用逗号隔开
    private String category;
    @SerializedName( "content_hash")
    private String contentHash;
    private String type;
    private String title;
    private String content;

    //时间戳
    private String created;
    @SerializedName( "abstract")
    private String abstractContent;  //其他版本该字段为：abstract
    private String dna;
    @SerializedName( "parent_dna")
    private String parentDna;

    private String language;
    private String source;
    private TreeMap<String, String> extra;
    private TreeMap<String, String> data;

    private Metadata.License license;

    public static class License {
        private String type;
        private TreeMap<String, String> parameters;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public TreeMap<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(TreeMap<String, String> parameters) {
            this.parameters = parameters;
        }
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public String getDna() {
        return dna;
    }

    public void setDna(String dna) {
        this.dna = dna;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TreeMap<String, String> getExtra() {
        return extra;
    }

    public void setExtra(TreeMap<String, String> extra) {
        this.extra = extra;
    }

    public String getParentDna() {
        return parentDna;
    }

    public void setParentDna(String parentDna) {
        this.parentDna = parentDna;
    }

    public TreeMap<String, String> getData() {
        return data;
    }

    public void setData(TreeMap<String, String> data) {
        this.data = data;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getBlockHash() {

        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }


    public String toJson() {
        return GsonUtil.getInstance().toJson(this);
    }

    /**
     * 获取metadata去除签名和dna的json字符串
     *
     * @return json字符串
     */
    public String toJsonRmSign() {
        String dna = this.dna;
        String signature = this.signature;
        String content = this.content;

        // sort attributes
        if (getLicense() != null) {
            getLicense().setParameters(MapUtil.sortMapByKey(getLicense().getParameters()));
        }
        if (getData() != null) {
            setData(MapUtil.sortMapByKey(getData()));
        }
        if (getExtra() != null) {
            setExtra(MapUtil.sortMapByKey(getExtra()));
        }

        this.dna = null;
        this.signature = null;
        this.content = null;

        Gson gson = GsonUtil.getInstance();

        JsonElement jsonElement = gson.toJsonTree(this);
        GsonUtil.sort(jsonElement);

        String json = gson.toJson(jsonElement);

        Set<String> htmlChars = Constants.HTML_SAFE_REPLACEMENT_CHARS.keySet();
        for (String s:htmlChars) {
            if (json.contains(s)){
                json = json.replace(s,Constants.HTML_SAFE_REPLACEMENT_CHARS.get(s));
            }
        }

        this.dna = dna;
        this.signature = signature;
        this.content = content;
        return json;
    }

}


