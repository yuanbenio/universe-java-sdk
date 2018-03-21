package com.yuanben.common;

import java.math.BigInteger;

public class Constants {
    public static final String STRING_EMPTY = "";

    public static final String Language_ZH = "zh-cn";

    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";

    private static final BigInteger SECP256K1N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);


    /**
     * Introduced in the Homestead release
     */
    public static BigInteger getSECP256K1N() {
        return SECP256K1N;
    }
}
