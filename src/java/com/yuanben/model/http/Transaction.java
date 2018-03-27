package com.yuanben.model.http;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * <p>原本链node节点返回的transaction封装</p>
 */
public class Transaction {
    @JSONField(name = "block_hash")
    private String blockHash;
    @JSONField(name = "block_height")
    private Long blockHeight;
    @JSONField(name = "data_height")
    private Long dataHeight;
    private String sender;
    private Long time;

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Long getDataHeight() {
        return dataHeight;
    }

    public void setDataHeight(Long dataHeight) {
        this.dataHeight = dataHeight;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
