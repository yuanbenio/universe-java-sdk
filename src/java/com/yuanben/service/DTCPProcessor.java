package com.yuanben.service;

import com.hankcs.hanlp.HanLP;
import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.model.Metadata;
import com.yuanben.util.SecretUtil;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

import java.util.List;
import java.util.UUID;

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
     * @param signature 16进制的metadata签名串
     * @return metadata的闪电dna
     * @throws InvalidException 入参为空
     */
    public static String GeneratorDNA(String signature) throws InvalidException {
        if (StringUtils.isBlank(signature)) {
            throw new InvalidException("signature is empty");
        }
        Keccak256 keccak256 = new Keccak256();
        keccak256.update(signature.getBytes());
        return Hex.toHexString(keccak256.digest());
    }

    /**
     * 对metadata签名 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * @param metadata metadata实例
     * @param privateKey 16进制的私钥
     * @return 16进制的metadata signature
     * @throws InvalidException 入参为空
     */
    public static String GenMetadataSignature(Metadata metadata, String privateKey) throws InvalidException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        return ECKeyProcessor.Sign(privateKey, metadata.toJsonRmSign().getBytes());
    }

    /**
     * 对metadata进行签名验证 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * @param metadata metadata
     * @return 验证结果
     * @throws InvalidException metadata为空
     */
    public static boolean VerifyMetadataSignature(Metadata metadata) throws InvalidException {
        if (metadata == null ) {
            throw new InvalidException("metadata is null");
        }
        return ECKeyProcessor.VerifySignature(metadata.getPubKey(),metadata.getSignature(), metadata.toJsonRmSign().getBytes());
    }

    /**
     * 对metadata进行补全
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata  必须包含content\title\type
     * @return 信息补全的metadata
     * @throws InvalidException
     */
    public static Metadata GenMetadataFromContent(String privateKey, Metadata metadata) throws InvalidException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        if (StringUtils.isBlank(metadata.getContent())) {
            throw new InvalidException("content is empty");
        }
        if (StringUtils.isBlank(metadata.getContentHash())) {
            metadata.setContentHash(GenContentHash(metadata.getContent()));
        }
        if (StringUtils.isBlank(metadata.getPubKey())) {
            metadata.setPubKey(ECKeyProcessor.GetPubKeyFromPri(privateKey));
        }
        if (StringUtils.isEmpty(metadata.getTitle())) {
            throw new InvalidException("title is empty");
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
                List<String> strings = HanLP.extractKeyword(metadata.getContent(), 5);
                String category = "";
                for (String s : strings) {
                    category += s + ",";
                }
                if (StringUtils.isNotBlank(category)) {
                    category = category.substring(0, category.length() - 1);
                }
                metadata.setCategory(category);
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


}
