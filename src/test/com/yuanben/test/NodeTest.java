package com.yuanben.test;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.*;
import com.yuanben.service.DTCPProcessor;
import com.yuanben.service.ECKeyProcessor;
import com.yuanben.service.NodeProcessor;
import org.junit.Test;

import java.util.TreeMap;

public class NodeTest {

    public String URL = "https://openapi.yuanbenlian.com";
    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    public String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    public String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";

    @Test
    public void QueryLicenseTest() {
        String licenseType = "cc";
        try {
            LicenseQueryResp licenseQueryResp = NodeProcessor.QueryLicense(URL, null, licenseType);
            if (licenseQueryResp == null) {
                System.out.println("结果体转换异常");
            } else if (Constants.CODE_ERROR.equalsIgnoreCase(licenseQueryResp.getCode())) {
                System.out.println("查询异常:" + licenseQueryResp.getMsg());
            } else {
                System.out.println("查询成功。" + licenseQueryResp.toJson());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void QueryMetadataTest() {
        String dna = "3Q7QAE45H6AUM95YCOGQ0GWVADF24G91YDLWII4E1WA2VWV012";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, null, dna);
            if (resp == null) {
                System.out.println("返回体转换异常");
            } else if (Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
                System.out.println("查询异常:" + resp.getMsg());
            } else {
                System.out.println("查询成功。" + resp.toJson());
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
        metadata.setType(Constants.TYPE_ARTICLE);

        metadata.setTitle("原本链测试");
        metadata.setParentDna("2QXZNC992KGDMMLU80YR5BMGMXTSNQI2ZPTN962J8ZBO4J1XNL");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        TreeMap<String, String> params = new TreeMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);

        TreeMap<String,String> data = new TreeMap<>();
        data.put("ext","jep");
        data.put("height","4000");
        data.put("original","http://meisuadci.oss-cn-beijing.aliyuncs.com/works/596c49fe-83a4-36e3-22b8-2555370c848e.JPG");

        TreeMap<String,String> extra = new TreeMap<>();
        extra.put("author","原本链");
        metadata.setExtra(extra);//2QUC32K30ZR4CPEZQEORHUH23XS65M522L6VDFZY39QEW2DURS   2QUC32K30ZR4CPEZQEORHUH23XS65M522L6VDFZY39QEW2DURS

        try {
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
        try {
            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, null,metadata);
            if (resp == null) {
                System.out.println("结果体转换异常");
            } else if (Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
                System.out.println("注册异常:" + resp.getMsg());
            } else {
                System.out.println("注册成功。" + resp.getData().getDna());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void QueryLatestBlockHashTest() {
        try {
            BlockHashQueryResp resp = NodeProcessor.QueryLatestBlockHash(URL, null);
            if (resp == null || Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
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
    public void ChechBlockHashTest() {
        try {
            BlockHashCheckReq req = new BlockHashCheckReq();
            req.setHash("4A7FCE024C64061D28BEB91A3FC935465BE54B3B");
            req.setHeight(22102L);
            BlockHashCheckResp resp = NodeProcessor.CheckBlockHash(URL, null, req);
            if (resp == null || Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
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
            subKeys[i]=ECKeyProcessor.GeneratorSecp256k1Key().getPublicKey();
        }
        try {
            RegisterAccountReq req = DTCPProcessor.GenRegisterAccountReq(private_key, subKeys);
            NodeProcessor.RegisterAccount("http://localhost:8081",null,req);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }
    
}
