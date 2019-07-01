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

import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.crypto.cryptohash.Keccak256;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.http.RegisterAccountReq;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.SecretUtil;
import com.yuanbenlian.util.StringUtils;
import org.spongycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import static com.yuanbenlian.service.ECKeyProcessor.Sign;

/**
 * <p>DTCP处理器</p>
 * <p>用于计算metadata，以及metadata中的各项值</p>
 * <p>
 * <p>DTCP processor</p>
 * <p>calculating metadata</p>
 */
public class DTCPProcessor {

    /**
     * 计算 contentHash
     * Calculating contentHash
     * <p>
     * 算法
     * contentHash = Keccak256(content)
     *
     * @return Hex contentHash
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
     * Calculating dna
     *
     * @param signature the signature of metadata
     * @return dna
     * @throws InvalidException signature is empty
     */
    public static String GeneratorDNA(String signature) throws InvalidException {
        if (StringUtils.isBlank(signature)) {
            throw new InvalidException("signature is empty");
        }
        byte[] bytes = ECKeyProcessor.Keccak256(Hex.decode(signature));
        String prefix = "";
        for (byte b : bytes) {
            if (b == 0) {
                prefix += "0";
            } else {
                break;
            }
        }
        return prefix + new BigInteger(Hex.toHexString(bytes), 16).toString(36).toUpperCase();
    }

    /**
     * 对metadata签名 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * sign(metadata)
     *
     * @param metadata   metadata
     * @param privateKey private key
     * @return signature
     * @throws InvalidException 入参为空
     */
    public static String GenMetadataSignature(Metadata metadata, String privateKey) throws InvalidException, UnsupportedEncodingException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        return Sign(privateKey, metadata.toJsonRmSign().getBytes("UTF-8"));
    }

    /**
     * 对metadata进行签名验证 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * verify metadata's signature
     *
     * @param metadata metadata
     * @return result
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
     * completing metadata
     *
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata   必须包含license\title\type\block_hash|block_height\category,
     *                   如果contentHash为空，则必须传入content的值；
     *                   如果type不是article，则必须传入contentHash;
     *                   如果type不是private，则必须传入category;
     *
     *                   include(license\title\type\block_hash|block_height|category.
     *                   if content is empty,you must pass content;
     *                   if type isn't article,you must pass contentHash;
     *                   if type isn't privatge,you must pass category)
     * @return full metadata
     * @throws InvalidException invalid parameters
     */
    public static Metadata FullMetadata(String privateKey, Metadata metadata) throws InvalidException, UnsupportedEncodingException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        if (StringUtils.isBlank(metadata.getContentHash())) {
            if (StringUtils.isBlank(metadata.getContent())) {
                throw new InvalidException("content and contentHash can't be empty at the same time");
            }
            metadata.setContentHash(GenContentHash(metadata.getContent()));
        }
        if (StringUtils.isEmpty(metadata.getBlockHash()) || StringUtils.isEmpty(metadata.getBlockHeight())) {
            throw new InvalidException("block hash or block height is empty");
        }
        if (StringUtils.isEmpty(metadata.getType())) {
            throw new InvalidException("type is empty");
        }
        if (StringUtils.isBlank(metadata.getPubKey())) {
            metadata.setPubKey(ECKeyProcessor.GetPubKeyFromPri(privateKey));
        }
        if (metadata.getLicense() == null || StringUtils.isBlank(metadata.getLicense().getType())) {
            throw new InvalidException("license is empty");
        }
        if (!metadata.getLicense().getType().equals("none") && (metadata.getLicense().getParameters() == null ||
                metadata.getLicense().getParameters().size() < 1)) {
            throw new InvalidException("license's parameters is empty");
        }
        if (StringUtils.isBlank(metadata.getLanguage())) {
            metadata.setLanguage(Constants.Language_ZH);
        }
        if (StringUtils.isBlank(metadata.getCreated())) {
            metadata.setCreated(Constants.STRING_EMPTY + System.currentTimeMillis() / 1000);
        }
        switch (metadata.getType()) {
            case Constants.TYPE_ARTICLE:
                if (StringUtils.isBlank(metadata.getAbstractContent())) {
                    metadata.setAbstractContent(StringUtils.isEmpty(metadata.getContent()) || metadata.getContent().length() < 200 ? metadata.getContent() :
                            metadata.getContent().substring(0, 200));
                }
                break;
            case Constants.TYPE_AUDIO:
            case Constants.TYPE_FILE:
            case Constants.TYPE_IMAGE:
            case Constants.TYPE_VIDEO:
                if (metadata.getData() == null || metadata.getData().size() < 1) {
                    throw new InvalidException("Please add extends data!");
                }
                break;
            case Constants.TYPE_PRIVATE:
            case Constants.TYPE_CUSTOM:
                break;
            default:
                throw new InvalidException("content type is nonsupport");
        }
        if (!metadata.getType().equals(Constants.TYPE_PRIVATE)) {
            if (StringUtils.isEmpty(metadata.getCategory())) {
                throw new InvalidException("category is empty");
            }
            if (StringUtils.isEmpty(metadata.getTitle())) {
                throw new InvalidException("title is empty");
            }
        }

        String sign = GenMetadataSignature(metadata, privateKey);
        String dna = GeneratorDNA(sign);
        metadata.setSignature(sign);
        metadata.setDna(dna);
        metadata.setContent(null);
        return metadata;

    }

    /**
     * 生成用于注册公钥的请求体
     * register public key
     *
     * @param privateKey 16进制私钥
     * @param subPubKeys 需要注册的公钥数组
     * @return request
     * @throws InvalidException invalid parameters
     */
    public static RegisterAccountReq GenRegisterAccountReq(String privateKey, String[] subPubKeys) throws InvalidException {
        if (subPubKeys == null || subPubKeys.length < 1 || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("subPubKeys or privateKey is illegal");
        }
        RegisterAccountReq req = new RegisterAccountReq();
        req.setSubPubKeys(subPubKeys);
        String pubKey = ECKeyProcessor.GetPubKeyFromPri(privateKey);
        String sign = ECKeyProcessor.Sign(privateKey, GsonUtil.getInstance().toJson(subPubKeys).getBytes());
        req.setPubKey(pubKey);
        req.setSignature(sign);
        return req;
    }
}
