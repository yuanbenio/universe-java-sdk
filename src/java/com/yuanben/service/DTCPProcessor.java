package com.yuanben.service;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.crypto.cryptohash.Keccak512;
import com.yuanben.model.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

/**
 * <p>DTCP处理器</p>
 * <p>用于计算metadata，以及metadata中的各项值</p>
 */
public class DTCPProcessor {

    /**
     * 生成contentHash
     * contentHash = Keccak256(content)
     * @return 16进制的contentHash
     */
    public static String GenContentHash(String... content){
        if (content == null) return Constants.STRING_EMPTY;
        Keccak256 keccak256 = new Keccak256();
        for (String s:content){
            keccak256.update(s.getBytes());
        }
        return Hex.toHexString(keccak256.digest());
    }

    public static String GenMetadataSignature(Metadata metadata,String privateKey) throws InvalidException {
        if (metadata == null || StringUtils.isBlank(privateKey)){
            throw new InvalidException("metadata or privateKey is null");
        }
        if (privateKey.length() != 64){
            throw new InvalidException("private key`s format is error");
        }
        return null;
    }


}
