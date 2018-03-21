package com.yuanben.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class Metadata {
    @JSONField(name = "pubkey")
    private String pubKey;
    @JSONField(name = "block_hash")
    private String blockHash;
    @JSONField(name = "block_height")
    private Long blockHeight;
    private String signature;
    private String id;


    //用逗号隔开
    private String category;
    @JSONField(name = "content_hash")
    private String contentHash;
    private String type;
    private String title;
    private String content;

    //时间戳
    private String created;
    @JSONField(name = "abstract")
    private String abstractContent;  //其他版本该字段为：abstract
    private String dna;
    @JSONField(name = "parent_dna")
    private String parentDna;

    private String language;
    private String source;
    private Object extra;
    private Object data;

    private Metadata.License license;

    public static class License {
        private String type;
        private Map<String, String> params;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
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

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public String getParentDna() {
        return parentDna;
    }

    public void setParentDna(String parentDna) {
        this.parentDna = parentDna;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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
        return JSONObject.toJSONString(this);
    }

    /**
     * 获取metadata去除签名和dna的json字符串
     *
     * @return json字符串
     */
    public String toJsonRmSign() {
        String dna = this.getDna();
        String signature = this.getSignature();
        this.dna = null;
        this.signature = null;
        String js = JSONObject.toJSONString(this);
        this.dna = dna;
        this.signature = signature;
        return js;
    }
}
