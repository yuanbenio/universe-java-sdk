package com.yuanben.service;

import com.yuanben.common.InvalidException;
import com.yuanben.crypto.ECKey;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.model.SecretKey;
import com.yuanben.util.SecretUtil;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * <p>密钥服务类</p>
 * <p>支持密钥对生成、签名、校验、私钥推导公钥</p>
 */
public class ECKeyProcessor {
    /**
     * 生成密钥对
     *
     * @return secp256k1密钥
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
     *
     * @param privateKey 16进制私钥
     * @return 16进制的公钥
     */
    public static String GetPubKeyFromPri(String privateKey) throws InvalidException {
        if (!SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("private key`s format is error");
        }
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();
        byte[] encoded = pubKeyPoint.getEncoded(true);
        return Hex.toHexString(encoded);
    }

    /**
     * 签名
     *
     * @param privateKey 私钥
     * @param content    签名内容 字节数组
     * @return 16进制签名串
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
        //只能对contentHash签名
        ECKey.ECDSASignature ecdsaSignature = ecKey.doSign(keccak256.digest());
        return ecdsaSignature.toHex();
    }

    /**
     * 签名验证
     *
     * @param publicKey 16进制的压缩公钥
     * @param signMsg   16进制的签名串
     * @param data      被签名的原数据字节数组 keccak256哈希值
     * @return 验证结果
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
