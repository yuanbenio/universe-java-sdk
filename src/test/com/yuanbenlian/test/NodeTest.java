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
package com.yuanbenlian.test;

import com.yuanbenlian.common.Constants;
import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.model.Metadata;
import com.yuanbenlian.model.http.*;
import com.yuanbenlian.service.DTCPProcessor;
import com.yuanbenlian.service.ECKeyProcessor;
import com.yuanbenlian.service.NodeProcessor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class NodeTest {

    private static final String OK = Constants.NODE_SUCCESS;

    public String URL = "https://testnet.yuanbenlian.com/v1";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    public String content = "The Yuanben Chain is a distributed underlying data network\n" +
            "The Chain is efficient, secure, easy to use, scalable, global, enterprise-level trusted alliance chain\n" +
            "Yuanben Chain supports sustainable on-chain data interaction and secures data which transfers through an intelligent contract and digital encryption algorithm\n" +
            "The Chain provides a worlds first unique ‘Yuanben Chain DNA’ which makes sure the data on the chain cannot be tampered with\n" +
            "The Chain has increased block-writing speeds to millisecond-levels by optimising the consensus mechanism and the original ‘Lightning DNA’ algorithm";

    public String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";

    @Test
    public void QueryLicenseTest() {
        String licenseType = "cc";
        try {
            LicenseQueryResp licenseQueryResp = NodeProcessor.QueryLicense(URL, licenseType, "v4.0");
            if (licenseQueryResp == null) {
                System.out.println("convert fail");
            } else if (Constants.NODE_SUCCESS.equalsIgnoreCase(licenseQueryResp.getCode())) {
                System.out.println("failure:" + licenseQueryResp.getMsg());
            } else {
                System.out.println("success。" + licenseQueryResp.toJson());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void QueryMetadataTest() {
        String dna = "3QKUIFVATBU8DBXSOUP3UN32EUWAYG4DFTRLKFFHYQYX8MK64V";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, dna);
            if (resp == null) {
                System.out.println("convert failure");
            } else if (Constants.NODE_SUCCESS.equalsIgnoreCase(resp.getCode())) {
                System.out.println("success。" + resp.toJson());
            } else {
                System.out.println("failure:" + resp.getMsg());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SaveMetadataTest() {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setBlockHeight("12345");
        metadata.setType(Constants.TYPE_CUSTOM);
        metadata.setCategory("test");

        metadata.setTitle("YuanBen chain test");
        metadata.setParentDna("2QXZNC992KGDMMLU80YR5BMGMXTSNQI2ZPTN962J8ZBO4J1XNL");
        Metadata.License license = new Metadata.License();
        license.setType("one-license");
        TreeMap<String, String> params = new TreeMap<>();
        params.put("sale", "no");
        license.setParameters(params);
        metadata.setLicense(license);

        TreeMap<String, String> data = new TreeMap<>();
        data.put("ext", "jep");
        data.put("height", "4000");
        data.put("original", "https://yuanbenlian.com/");

        TreeMap<String, String> extra = new TreeMap<>();
        extra.put("author", "YuanBen chain");
        metadata.setExtra(extra);

        try {
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);
        } catch (InvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println(metadata.toJson());
        try {
            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, metadata);
            if (resp == null) {
                System.out.println("convert failure");
            } else if (OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("success。" + resp.getData().getDna());
            } else {
                System.out.println("failure:" + resp.getMsg());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void QueryLatestBlockHashTest() {
        try {
            BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL);
            if (resp == null || !OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("query failure :" + resp.getMsg());
            } else {
                System.out.println("latest block hash:" + resp.getData().getLatestBlockHash());
                System.out.println("latest block height:" + resp.getData().getLatestBlockHeight());
                System.out.println("latest block time:" + resp.getData().getLatestBlockTime());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CheckBlockHashTest() {
        try {
            BlockHashCheckReq req = new BlockHashCheckReq();
            req.setHash("4A7FCE024C64061D28BEB91A3FC935465BE54B3B");
            req.setHeight(22102L);
            BlockHashCheckResp resp = NodeProcessor.CheckBlockHash(URL, req);
            if (resp == null || !OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("query failure :" + resp.getMsg());
            } else {
                System.out.println("check result:" + resp.getData());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void RegisterAccountTest() {
        String[] subKeys = new String[2];
        for (int i = 0; i < 2; i++) {
            subKeys[i] = ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey();
        }
        try {
            RegisterAccountReq req = DTCPProcessor.GenRegisterAccountReq(private_key, subKeys);
            NodeProcessor.RegisterAccount("http://localhost:8081/v1", req);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
}


