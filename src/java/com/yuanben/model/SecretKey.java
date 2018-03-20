package com.yuanben.model;

public class SecretKey {

    //16进制的私钥  长度64 chars
    private String privateKey;
    //16进制的压缩公钥 长度66 chars
    private String publicKey;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
