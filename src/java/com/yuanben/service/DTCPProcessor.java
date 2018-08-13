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

package com.yuanben.service;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.RegisterAccountReq;
import com.yuanben.util.Base36;
import com.yuanben.util.GsonUtil;
import com.yuanben.util.SecretUtil;
import com.yuanben.util.StringUtils;
import org.spongycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;

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
        return Base36.EncodeBytes(ECKeyProcessor.Keccak256(Hex.decode(signature)));
    }

    /**
     * 对metadata签名 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     *
     * @param metadata   metadata实例
     * @param privateKey 16进制的私钥
     * @return 16进制的metadata signature
     * @throws InvalidException 入参为空
     */
    public static String GenMetadataSignature(Metadata metadata, String privateKey) throws InvalidException, UnsupportedEncodingException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        return Sign(privateKey, metadata.toJsonRmSign().getBytes("utf-8"));
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
     * @param metadata   必须包含license\title\type\block_hash|block_height\category,如果contentHash为空，则必须传入content的值；如果type不是article，则必须传入contentHash;如果category为空，则必须传入content
     * @return 信息补全的metadata
     * @throws InvalidException 参数错误
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
        String sign = ECKeyProcessor.Sign(privateKey, GsonUtil.getInstance().toJson(subPubKeys).getBytes());
        req.setPubKey(pubKey);
        req.setSignature(sign);
        return req;
    }
}
