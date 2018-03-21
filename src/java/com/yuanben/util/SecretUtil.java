package com.yuanben.util;

import org.apache.commons.lang3.StringUtils;

public class SecretUtil {

    /**
     * 私钥检查
     *
     * @param privateKey 16进制的私钥
     * @return 成功与否
     */
    public static boolean CheckPrivateKey(String privateKey) {
        if (StringUtils.isBlank(privateKey) || privateKey.length() != 64) {
            return false;
        }
        return true;
    }

    /**
     * 私钥检查
     *
     * @param privateKey 字节数组形式的私钥
     * @return 成功与否
     */
    public static boolean CheckPrivateKey(byte[] privateKey) {
        if (privateKey == null || privateKey.length != 32) {
            return false;
        }
        return true;
    }

    /**
     * 公钥检查
     *
     * @param publicKey    字节数组形式的公钥
     * @param isCompressed 是否是压缩公钥
     * @return 成功与否
     */
    public static boolean CheckPublicKey(byte[] publicKey, boolean isCompressed) {
        if (publicKey == null) {
            return false;
        }
        return isCompressed ? publicKey.length == 33 : publicKey.length == 65;
    }

    /**
     * 公钥检查
     *
     * @param publicKey    16进制的公钥
     * @param isCompressed 是否是压缩公钥
     * @return 成功与否
     */
    public static boolean CheckPublicKey(String publicKey, boolean isCompressed) {
        if (publicKey == null) {
            return false;
        }
        return isCompressed ? publicKey.length() == 66 : publicKey.length() == 130;
    }

    /**
     * 公钥检查
     *
     * @param publicKey 16进制的公钥
     * @return 成功与否
     */
    public static boolean CheckPublicKey(String publicKey) {
        if (publicKey == null) {
            return false;
        }
        return publicKey.length() == 66 || publicKey.length() == 130;
    }

    /**
     * 公钥检查
     *
     * @param publicKey 字节数组形式的公钥
     * @return 成功与否
     */
    public static boolean CheckPublicKey(byte[] publicKey) {
        if (publicKey == null) {
            return false;
        }
        return publicKey.length == 33 || publicKey.length == 65;
    }


}
