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
package com.yuanben.test;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.RegisterAccountReq;
import com.yuanben.service.DTCPProcessor;
import com.yuanben.service.ECKeyProcessor;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

/**
 * DTCPProcessor Test
 */
public class DTCPTest {
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    public String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";
    public String sign_msg = "78c9082f22451fb9befd9adc8ebffa6e06599b94d4846cf7e52f45e92e42f16f2e5a8e411b560659735d13f4e2fa79031cf2a8dc752fe21f579c9ab5409e240e00";

    public void GenContentHashTest() {
        String contentHash = DTCPProcessor.GenContentHash(content);
        System.out.println(contentHash);
    }


    public void GenMetadataFromContentTest() {
        GenMetadataFromContent();
    }

    public Metadata GenMetadataFromContent() {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setBlockHeight("22102");
        metadata.setType(Constants.TYPE_ARTICLE);
        metadata.setTitle("原本链java版本sdk测试");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        TreeMap<String, String> params = new TreeMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);
        try {
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);
            System.out.println(metadata.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return metadata;
    }

    public void GenMetadataSignatureTest() {
        Metadata metadata = GenMetadataFromContent();
        try {
            String signature = DTCPProcessor.GenMetadataSignature(metadata, private_key);
            System.out.println(signature);
        } catch (InvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void GeneratorDNATest() {
        try {
            String dna = DTCPProcessor.GeneratorDNA(sign_msg);
            System.out.println(dna);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void VerifyMetadataSignatureTest() {

        try {
            Metadata metadata = GenMetadataFromContent();
            boolean b = DTCPProcessor.VerifyMetadataSignature(metadata);
            System.out.println(b);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void GenRegisterAccountReqTest() {
        String[] subKeys = new String[5];
        for (int i = 0; i < 5; i++) {
            subKeys[i] = ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey();
        }
        try {
            RegisterAccountReq req = DTCPProcessor.GenRegisterAccountReq(private_key, subKeys);
            System.out.println(req.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
}
