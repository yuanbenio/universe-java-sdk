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

package com.yuanbenlian.service;

import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.crypto.ECKey;
import com.yuanbenlian.crypto.cryptohash.Keccak256;
import com.yuanbenlian.model.SecretKey;
import com.yuanbenlian.util.SecretUtil;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * <p>密钥服务类</p>
 * <p>支持密钥对生成、签名、校验、私钥推导公钥</p>
 *
 * <p>secret key service</p>
 * <p>this is a base service, it support: generate key pair、calculating signature、verify signature、etc</p>
 */
public class ECKeyProcessor {
    /**
     * 生成密钥对
     * generate a key pair
     *
     * @return secp256k1 key pair
     */
    public static SecretKey GeneratorSecp256k1Key() {
        SecretKey secretKey = new SecretKey();
        ECKey ecKey = new ECKey();
        secretKey.setPrivateKey(Hex.toHexString(ecKey.getPrivKeyBytes()));
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();
        byte[] encoded = pubKeyPoint.getEncoded(true);
        secretKey.setPublicKey(Hex.toHexString(encoded));
        return secretKey;
    }

    /**
     * 根据私钥生成16进制对压缩公钥
     * calculating public key by private key
     *
     * @param privateKey
     * @return public key
     */
    public static String GetPubKeyFromPri(String privateKey) throws InvalidException {
        if (!SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("Incorrect private key");
        }
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();
        byte[] encoded = pubKeyPoint.getEncoded(true);
        return Hex.toHexString(encoded);
    }

    /**
     * 根据公钥生成16进制对压缩公钥
     * calculating address by public key
     *
     * @param publicKey
     * @return address
     */
    public static String Address(String publicKey) throws InvalidException {
        if (!SecretUtil.CheckPublicKey(publicKey)) {
            throw new InvalidException("Incorrect public key");
        }

        ECKey ecKey = ECKey.fromPublicOnly(Hex.decode(publicKey));

        return "0x"+Hex.toHexString(ecKey.getAddress()).toUpperCase();
    }

    /**
     * 签名
     * sign
     *
     * @param privateKey
     * @param content
     * @return signature
     */
    public static String Sign(String privateKey, byte[]... content) throws InvalidException {
        if (!SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("private key`s format is error");
        }
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        Keccak256 keccak256 = new Keccak256();
        for (byte[] b : content) {
            keccak256.update(b);
        }
        ECKey.ECDSASignature ecdsaSignature = ecKey.sign(keccak256.digest());
        return ecdsaSignature.toHex();
    }

    /**
     * 签名验证
     * verify signature
     *
     * @param publicKey
     * @param signMsg  signature
     * @param data      被签名的原数据字节数组 keccak256哈希值
     *                  source data
     * @return result
     */
    public static boolean VerifySignature(String publicKey, String signMsg, byte[] data) throws InvalidException {
        if (!SecretUtil.CheckPublicKey(publicKey, true)) {
            throw new InvalidException("public key`s format is error");
        }

        byte recID = Byte.parseByte(signMsg.substring(signMsg.length() - 2), 16);
        byte[] decode = Hex.decode(signMsg.substring(0, signMsg.length() - 2));

        byte[] rBs = new byte[decode.length / 2];
        byte[] sBs = new byte[decode.length / 2];

        System.arraycopy(decode, 0, rBs, 0, decode.length / 2);
        System.arraycopy(decode, decode.length / 2, sBs, 0, decode.length / 2);

        BigInteger r = new BigInteger(Hex.toHexString(rBs), 16);
        BigInteger s = new BigInteger(Hex.toHexString(sBs), 16);


        ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(r.toByteArray(), s.toByteArray(), recID);

        return ECKey.verify(data, sig, Hex.decode(publicKey));
    }


    /**
     * keccak256哈希运算
     *
     * @param content 内容
     * @return 哈希值 length=32
     */
    public static byte[] Keccak256(byte[]... content) {
        if (content == null) return null;
        Keccak256 keccak256 = new Keccak256();
        for (byte[] s : content) {
            keccak256.update(s);
        }
        return keccak256.digest();
    }

    /**
     * keccak256哈希运算
     *
     * @param content 内容
     * @return 哈希值 length=32
     */
    public static byte[] Keccak256(String... content) {
        if (content == null) return null;
        Keccak256 keccak256 = new Keccak256();
        for (String s : content) {
            keccak256.update(s.getBytes());
        }
        return keccak256.digest();
    }
}
