package com.yuanbenlian.model.http;

import com.yuanbenlian.util.GsonUtil;

public class LicenseQueryReq {
    private String type;
    private String version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String toJson() {
        return GsonUtil.getInstance().toJson(this);
    }

}
