package com.yuanben.service;

import com.alibaba.fastjson.JSONArray;
import com.hankcs.hanlp.HanLP;
import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.RegisterAccountReq;
import com.yuanben.util.SecretUtil;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static com.yuanben.service.ECKeyProcessor.Sign;

/**
 * <p>DTCP处理器</p>
 * <p>用于计算metadata，以及metadata中的各项值</p>
 */
public class DTCPProcessor {

    /**
     * 生成contentHash
     * contentHash = Keccak256(content)
     *
     * @return 16进制的contentHash
     */
    public static String GenContentHash(String... content) {
        if (content == null) return Constants.STRING_EMPTY;
        Keccak256 keccak256 = new Keccak256();
        for (String s : content) {
            keccak256.update(s.getBytes());
        }
        return Hex.toHexString(keccak256.digest());
    }

    /**
     * 生成闪电dna
     *
     * @param signature 16进制的metadata签名串
     * @return metadata的闪电dna
     * @throws InvalidException 入参为空
     */
    public static String GeneratorDNA(String signature) throws InvalidException {
        if (StringUtils.isBlank(signature)) {
            throw new InvalidException("signature is empty");
        }
        return new BigInteger(Hex.toHexString(ECKeyProcessor.Keccak256(Hex.decode(signature))), 16).toString(36).toUpperCase();
    }

    /**
     * 对metadata签名 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     *
     * @param metadata   metadata实例
     * @param privateKey 16进制的私钥
     * @return 16进制的metadata signature
     * @throws InvalidException 入参为空
     */
    public static String GenMetadataSignature(Metadata metadata, String privateKey) throws InvalidException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        return Sign(privateKey, metadata.toJsonRmSign().getBytes());
    }

    /**
     * 对metadata进行签名验证 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     *
     * @param metadata metadata
     * @return 验证结果
     * @throws InvalidException metadata为空
     */
    public static boolean VerifyMetadataSignature(Metadata metadata) throws InvalidException {
        if (metadata == null) {
            throw new InvalidException("metadata is null");
        }
        return ECKeyProcessor.VerifySignature(metadata.getPubKey(), metadata.getSignature(), ECKeyProcessor.Keccak256(metadata.toJsonRmSign()));
    }

    /**
     * 对metadata进行补全
     *
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata   必须包含license\title\type\block_hash|block_height,如果contentHash为空，则必须传入content的值；如果type不是article，则必须传入contentHash
     * @return 信息补全的metadata
     * @throws InvalidException 参数错误
     */
    public static Metadata FullMetadata(String privateKey, Metadata metadata) throws InvalidException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        if (StringUtils.isBlank(metadata.getContentHash())) {
            if (! Constants.TYPE_ARTICLE.equals(metadata.getType())){
                throw new InvalidException("there must be a contentHash if the content type is image、video or audio");
            }
            if (StringUtils.isBlank(metadata.getContent())) {
                throw new InvalidException("content is empty");
            }
            metadata.setContentHash(GenContentHash(metadata.getContent()));
        }
        if (StringUtils.isBlank(metadata.getPubKey())) {
            metadata.setPubKey(ECKeyProcessor.GetPubKeyFromPri(privateKey));
        }
        if (StringUtils.isEmpty(metadata.getTitle())) {
            throw new InvalidException("title is empty");
        }
        if (StringUtils.isEmpty(metadata.getBlockHash()) || StringUtils.isEmpty(metadata.getBlockHeight())) {
            throw new InvalidException("block hash or block height is empty");
        }
        if (StringUtils.isEmpty(metadata.getType())) {
            throw new InvalidException("type is empty");
        }
        if (metadata.getLicense() == null || StringUtils.isBlank(metadata.getLicense().getType())) {
            throw new InvalidException("license is empty");
        }
        if (StringUtils.isBlank(metadata.getId())) {
            metadata.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (StringUtils.isBlank(metadata.getLanguage())) {
            metadata.setLanguage(Constants.Language_ZH);
        }
        if (StringUtils.isBlank(metadata.getCreated())) {
            metadata.setCreated(Constants.STRING_EMPTY + System.currentTimeMillis());
        }
        switch (metadata.getType()) {
            case Constants.TYPE_ARTICLE:
                if (StringUtils.isBlank(metadata.getAbstractContent())) {
                    metadata.setAbstractContent(metadata.getContent().length() < 200 ? metadata.getContent() :
                            metadata.getContent().substring(0, 200));
                }
                if (StringUtils.isNotBlank(metadata.getContent())) {
                    List<String> strings = HanLP.extractKeyword(metadata.getContent(), 5);
                    String category = "";
                    for (String s : strings) {
                        category += s + ",";
                    }
                    if (StringUtils.isNotBlank(category)) {
                        category = category.substring(0, category.length() - 1);
                    }
                    metadata.setCategory(category);
                }
                break;
            case Constants.TYPE_AUDIO:
            case Constants.TYPE_IMAGE:
            case Constants.TYPE_VIDEO:
                if (StringUtils.isBlank(metadata.getContentHash())) {
                    throw new InvalidException("there must be a contentHash if the content type is image、video or audio");
                }
                break;
            default:
                throw new InvalidException("content type is nonsupport");
        }
        String sign = GenMetadataSignature(metadata, privateKey);
        String dna = GeneratorDNA(sign);
        metadata.setSignature(sign);
        metadata.setDna(dna);
        //node节点不需要content
        metadata.setContent(null);
        return metadata;

    }

    /**
     * 生成用于注册公钥的请求体
     *
     * @param privateKey 16进制私钥
     * @param subPubKeys 需要注册的公钥数组
     * @return 请求体封装
     * @throws InvalidException 参数错误
     */
    public static RegisterAccountReq GenRegisterAccountReq(String privateKey, String[] subPubKeys) throws InvalidException {
        if (subPubKeys == null || subPubKeys.length < 1 || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("subPubKeys or privateKey is illegal");
        }
        RegisterAccountReq req = new RegisterAccountReq();
        req.setSubPubKeys(subPubKeys);
        String pubKey = ECKeyProcessor.GetPubKeyFromPri(privateKey);
        String sign = ECKeyProcessor.Sign(privateKey, JSONArray.toJSONString(subPubKeys).getBytes());
        req.setPubKey(pubKey);
        req.setSignature(sign);
        return req;
    }
}
