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
import com.yuanben.model.http.*;
import com.yuanben.service.DTCPProcessor;
import com.yuanben.service.ECKeyProcessor;
import com.yuanben.service.NodeProcessor;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class NodeTest {

    private static final String OK = "ok";

    public String URL = "https://testnet.yuanbenlian.com";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    public String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    public String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";

    public void QueryLicenseTest() {
        String licenseType = "cc";
        try {
            LicenseQueryResp licenseQueryResp = NodeProcessor.QueryLicense(URL, null, licenseType);
            if (licenseQueryResp == null) {
                System.out.println("结果体转换异常");
            } else if ("ok".equalsIgnoreCase(licenseQueryResp.getCode())) {
                System.out.println("查询异常:" + licenseQueryResp.getMsg());
            } else {
                System.out.println("查询成功。" + licenseQueryResp.toJson());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void QueryMetadataTest() {
        String dna = "5546IYOJT02P3SDJWOQYNMVC1F0R7SCMUQFMR9P9QW2J2G59CJ";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, null, dna);
            if (resp == null) {
                System.out.println("返回体转换异常");
            } else if (OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("查询成功。" + resp.toJson());
            } else {
                System.out.println("查询异常:" + resp.getMsg());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void SaveMetadataTest() {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setBlockHeight("12345");
        metadata.setType(Constants.TYPE_CUSTOM);
        metadata.setCategory("原本链测试");

        metadata.setTitle("原本链测试");
        metadata.setParentDna("2QXZNC992KGDMMLU80YR5BMGMXTSNQI2ZPTN962J8ZBO4J1XNL");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        TreeMap<String, String> params = new TreeMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);

        TreeMap<String, String> data = new TreeMap<>();
        data.put("ext", "jep");
        data.put("height", "4000");
        data.put("original", "https://yuanbenlian.com/");

        TreeMap<String, String> extra = new TreeMap<>();
        extra.put("author", "原本链");
        metadata.setExtra(extra);

        try {
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);
        } catch (InvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /*String str = "{\"pubkey\":\"02bf15f3abca71e9f4b6c836f597fdf760bc786bedaa02387f11d0709eaae055ee\",\"block_hash\":\"A89659D1444A03C41FDA0D59C9367C53ECCEB06E\",\"block_height\":\"210576\",\"signature\":\"61feab9dc32826fa5189e0ac978e91ee453f779fe4f2809986816cafe6fec487fc74512a5d255bbed8116999f7f3a2aef1c11b2566c60b0f12b5323007110200\",\"category\":\"货物跟踪\",\"content_hash\":\"7ec0a5ccd195aecf642f0d03f15e77cdcd00ac231f7e8879d77bcf84ca2b7bdc\",\"type\":\"custom\",\"title\":\"卓志海上丝路-原本链-订舱物流轨迹-1.0\",\"created\":\"1534232603\",\"dna\":\"QHUXOKRS0YQH94WMO7AO7QTCZ2U97LZL5W5SJRK4CNVN5GQTL\",\"language\":\"zh-CN\",\"extra\":{\"destAddress\":\"TEMA\",\"destCountry\":\"Ghana\",\"orderNo\":\"BK18081400002\",\"originAddress\":\"NANSHA\",\"originCountry\":\"China\",\"status\":\"生成订舱单\",\"time\":\"2018-08-14 15:43:23.557\",\"tracks\":\"【牛测】【一刀】提交订舱单\"},\"license\":{\"type\":\"cc\",\"parameters\":{\"bb\":\"22\",\"yy\":\"44\"}}}";

        metadata = GsonUtil.getInstance().fromJson(str, Metadata.class);
        System.out.println(metadata.getSignature());

        try {
            DTCPProcessor.FullMetadata("2a9511a7a90f5839318a0ecd85883800c1f58ec154e1a50b450dd3e8405766a7",metadata);
        } catch (InvalidException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println(metadata.getSignature());


        System.out.println(metadata.toJson());*/
        try {
            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, null, metadata);
            if (resp == null) {
                System.out.println("结果体转换异常");
            } else if (OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("注册成功。" + resp.getData().getDna());
            } else {
                System.out.println("注册异常:" + resp.getMsg());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void QueryLatestBlockHashTest() {
        try {
            BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL, null);
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

    public void ChechBlockHashTest() {
        try {
            BlockHashCheckReq req = new BlockHashCheckReq();
            req.setHash("4A7FCE024C64061D28BEB91A3FC935465BE54B3B");
            req.setHeight(22102L);
            BlockHashCheckResp resp = NodeProcessor.CheckBlockHash(URL, null, req);
            if (resp == null || !OK.equalsIgnoreCase(resp.getCode())) {
                System.out.println("query failure :" + resp.getMsg());
            } else {
                System.out.println("check result:" + resp.getData());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    public void RegisterAccountTest() {
        String[] subKeys = new String[2];
        for (int i = 0; i < 2; i++) {
            subKeys[i] = ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey();
        }
        try {
            RegisterAccountReq req = DTCPProcessor.GenRegisterAccountReq(private_key, subKeys);
            NodeProcessor.RegisterAccount("http://localhost:8081", null, req);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    /*public static void main (String[] args) {
        NodeTest nodeTest = new NodeTest();
        while (true) {
            nodeTest.SaveMetadataTest();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
    }*/
}
