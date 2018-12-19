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
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.http.*;
import com.yuanbenlian.util.GsonUtil;
import com.yuanbenlian.util.HttpUtil;
import com.yuanbenlian.util.SecretUtil;
import com.yuanbenlian.util.StringUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * <p>原本链node节点处理器</p>
 * <p>支持向node节点发送和查询metadata，以及查询license</p>
 * <p>
 * <p>To Access YuanBen Chain Node</p>
 * <p>support:query and save metadata、query license、query latest blockHash、register public key</p>
 */
public class NodeProcessor {

    /**
     * 向node节点查询metadata
     * query metadata from YuanBen chain
     *
     * @param url node address （http://localhost:9000/v1)
     * @param dna DNA
     * @return result include metadata and transaction information
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static MetadataQueryResp QueryMetadata(String url, String dna) throws InvalidException {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(dna)) {
            throw new InvalidException("url or DNA is empty");
        }
        url += "/metadata/" + dna;
        String s = HttpUtil.sendGet(url);
        return GsonUtil.getInstance().fromJson(s, MetadataQueryResp.class);
    }

    /**
     * 向node节点注册metadata
     * submit metadata to YuanBen chain
     *
     * @param url node address（http://localhost:9000/v1)
     * @param md  metadata
     * @return result
     * @throws InvalidException
     */
    public static MetadataSaveResp SaveMetadata(String url, Metadata md) throws InvalidException {
        if (StringUtils.isBlank(url)) {
            throw new InvalidException("url is empty");
        }
        url += "/metadata";
        if (md == null) {
            throw new InvalidException("metadata is null");
        }
        if (StringUtils.isBlank(md.getSignature())) {
            throw new InvalidException("signature is null");
        }
        if (md.getLicense() == null || (!Metadata.License.NoneLicense.getType().equals(md.getLicense().getType()) && MapUtils.isEmpty(md.getLicense().getParameters()))) {
            throw new InvalidException("license is null");
        }
        String s = HttpUtil.sendPost(url, md.toJson());
        return GsonUtil.getInstance().fromJson(s, MetadataSaveResp.class);
    }

    /**
     * 向node节点查询license
     * query license to YuanBen chain
     *
     * @param url         node address （http://localhost:9000/v1)
     * @param licenseType license's type
     * @return result
     * @throws InvalidException
     */
    public static LicenseQueryResp QueryLicense(String url, String licenseType, String licenseVersion) throws InvalidException {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(licenseType) || StringUtils.isBlank(licenseVersion)) {
            throw new InvalidException("the parameters are empty");
        }
        LicenseQueryReq req = new LicenseQueryReq();
        req.setType(licenseType);
        req.setVersion(licenseVersion);

        url += "/queryLicense";
        String s = HttpUtil.sendPost(url, req.toJson());
        return GsonUtil.getInstance().fromJson(s, LicenseQueryResp.class);

    }

    /**
     * 向node节点查询最新的blockHash
     * query latest blockHash and blockHeight to YuanBen chain
     *
     * @param url node address （http://localhost:9000/v1)
     * @return result
     * @throws InvalidException
     */
    public static BlockHashQueryResp QueryLatestBlockHash(String url) throws InvalidException {
        if (StringUtils.isBlank(url)) {
            throw new InvalidException("url is empty");
        }
        url += "/block_hash";
        String s = HttpUtil.sendGet(url);
        return GsonUtil.getInstance().fromJson(s, BlockHashQueryResp.class);
    }

    /**
     * 向node节点查询blockHash是否在链上，并处于指定高度
     * Query to the node whether blockHash is on the chain and at the specified height
     *
     * @param url node address（http://localhost:9000/v1)
     * @param req request include blockHash and blockHeight
     * @return result
     * @throws InvalidException
     */
    public static BlockHashCheckResp CheckBlockHash(String url, BlockHashCheckReq req) throws InvalidException {
        if (StringUtils.isBlank(url) || req == null) {
            throw new InvalidException("url or request body is empty");
        }

        url += "/check_block_hash";
        String s = HttpUtil.sendPost(url, req.toJson());
        return GsonUtil.getInstance().fromJson(s, BlockHashCheckResp.class);
    }

    /**
     * 注册公钥
     * register public key to YuanBen chain
     *
     * @param url node address （http://localhost:9000/v1)
     * @param req request
     * @return result
     * @throws InvalidException
     */
    public static RegisterAccountResp RegisterAccount(String url, RegisterAccountReq req) throws InvalidException {
        if (StringUtils.isBlank(url) || req == null) {
            throw new InvalidException("url or request body is empty");
        }
        if (!SecretUtil.CheckPublicKey(req.getPubKey(), true) ||
                StringUtils.isEmpty(req.getSignature()) ||
                req.getSubPubKeys() == null || req.getSubPubKeys().length < 1) {
            throw new InvalidException("invalid parameters");
        }
        url += "/accounts";
        String s = HttpUtil.sendPost(url, req.toJson());
        return GsonUtil.getInstance().fromJson(s, RegisterAccountResp.class);
    }
}
