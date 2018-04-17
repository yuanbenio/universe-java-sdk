package com.yuanben.examples;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.*;
import com.yuanben.service.DTCPProcessor;
import com.yuanben.service.ECKeyProcessor;
import com.yuanben.service.NodeProcessor;

import java.util.TreeMap;

public class NodeTest {

//    private static String URL = "http://localhost:8080";
    private static String URL = "https://openapi.yuanbenlian.com";
    private static String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    private static String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    private static String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    private static String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";

    //examples result:
    //查询成功。{"code":"ok","data":{"description":"Creative Commons","id":"123456","type":"cc","parameters":[{"values":"y,n,sa","name":"adaptation","description":"是否允许演绎","type":"select"},{"values":"y,n","name":"commercial","description":"是否允许商用","type":"select"},{"values":"0","name":"expire","description":"有效期","type":"timestamp"},{"values":"0","name":"price","description":"授权价格","type":"decimal"}],"version":"4.0"},"tx":{"block_hash":"61218194b96a6f768e75c171b8ce3a8b9287b84e","block_height":1105,"data_height":13,"sender":"01218d7e82a0f2c4b31d7089a4dee33deba34899cc3924e99c1cd32d71ba25eb3a","time":1522039676}}
    private static void QueryLicenseTest() {
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

    //examples result:
    //查询成功。{"code":"ok","data":{"data":{},"extra":{},"license":{}},"tx":{"block_hash":"d7f2d8f9298165bff258d1672003cdd3c20c8023","block_height":285,"data_height":4,"sender":"013887f8340163832be8cc5fdeb617462b599ccd4073c05aaf9bb5d187e3b57170","time":1522221709}}
    private static void QueryMetadataTest() {
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

    //example result:
//    true
//    {"abstract":"原本链是一个分布式的底层数据网络；原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别","block_hash":"4D36473D2FF1FE0772A6C0C55D7911295D8E1E27","block_height":"12345","category":"原本,数据,DNA,安全,区块","content_hash":"54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526","created":"1522386475044","id":"81300bedc6fb4771b1dbbe1801d991b6","language":"zh-cn","license":{"parameters":{"b":"2","y":"4"},"type":"cc"},"pubkey":"03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77","title":"原本链java版本sdk测试","type":"article"}
//    注册成功。423Z4LIH0YV9NUKYCEVTTKYLJMK949VC543T5CC050LCU8HBJ5
    private static void SaveMetadataTest() {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setBlockHeight("12345");
        metadata.setType(Constants.TYPE_ARTICLE);
        metadata.setTitle("原本链java版本sdk测试");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);

        try {
            metadata = DTCPProcessor.FullMetadata(private_key, metadata);
        } catch (InvalidException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(DTCPProcessor.VerifyMetadataSignature(metadata));
        } catch (InvalidException e) {
            e.printStackTrace();
        }
        try {
            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, null, true,metadata);
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


    //test reuslt:
    //latest block hash:4A7FCE024C64061D28BEB91A3FC935465BE54B3B
    //latest block height:22102
    //latest block time:2018-03-27T00:10:21.682Z
    public static void QueryLatestBlockHashTest() {
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

    //test result :
    //check result:true
    public static void ChechBlockHashTest() {
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


    //rest result:
    //{"code":"ok"}
    private static void RegisterAccountTest() {
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


    public static void main(String[] args) {
        SaveMetadataTest();

    }
}
