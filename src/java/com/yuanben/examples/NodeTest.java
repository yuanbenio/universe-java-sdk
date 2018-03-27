package com.yuanben.examples;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.model.Metadata;
import com.yuanben.model.http.*;
import com.yuanben.service.DTCPProcessor;
import com.yuanben.service.ECKeyProcessor;
import com.yuanben.service.NodeProcessor;

import java.util.HashMap;

public class NodeTest {

    private static String URL = "http://119.23.22.129:9000";
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

    private static void QueryMetadataTest() {
        String dna = "3Y8DCROXXKLDSN3TPSBS20FELZA5PE26A001YKR5EAHHUJIXEX";
        try {
            MetadataQueryResp resp = NodeProcessor.QueryMetadata(URL, null, dna);
            if (resp == null) {
                System.out.println("结果体转换异常");
            } else if (Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
                System.out.println("查询异常:" + resp.getMsg());
            } else {
                System.out.println("查询成功。" + resp.toJson());
            }
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    private static void SaveMetadataTest() {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setType(Constants.TYPE_ARTICLE);
        metadata.setTitle("原本链java版本sdk测试");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        HashMap<String, Object> params = new HashMap<>();
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
        System.out.println(metadata.toJsonRmSign());
        try {
            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, null, metadata);
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


    public static void test3() throws InvalidException {
        Metadata metadata = new Metadata();
        metadata.setAbstractContent("原本链是一个分布式的底层数据网络；原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别");
        metadata.setBlockHash("4D36473D2FF1FE0772A6C0C55D7911295D8E1E27");
        metadata.setCategory("原本,数据,DNA,安全,区块");
        metadata.setContentHash("54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526");
        metadata.setCreated("1522067969129");
        metadata.setId("4c9b26e165344cf391822cb4c221e8b5");
        metadata.setLanguage("zh-cn");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        HashMap<String, Object> params = new HashMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);
        metadata.setPubKey("03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77");
        metadata.setTitle("原本链java版本sdk测试");
        metadata.setType("article");
        metadata.setSignature("ffd1515581f7962444291faf67f27f3ef13b9401f52ba076f2cd5b25f88341a923492d08b82cf7b6801c5d8e840bc771d960e74bde50c79387e151f8f86079b601");
        metadata.setDna("65SO0BNCXRLWIEIKVFSZAAL8WG2964P91N3S29T8HS3YP1RQ67");

        System.out.println("sign:" + DTCPProcessor.GenMetadataSignature(metadata, private_key));

        System.out.println("pub from priKey :" + ECKeyProcessor.GetPubKeyFromPri(private_key));


        System.out.println("sign:" + ECKeyProcessor.Sign(private_key, metadata.toJsonRmSign().getBytes()));

        boolean b = ECKeyProcessor.VerifySignature(public_key, DTCPProcessor.GenMetadataSignature(metadata, private_key), ECKeyProcessor.Keccak256(metadata.toJsonRmSign()));
        System.out.println("v:" + b);

        b = ECKeyProcessor.VerifySignature(public_key, "ffd1515581f7962444291faf67f27f3ef13b9401f52ba076f2cd5b25f88341a923492d08b82cf7b6801c5d8e840bc771d960e74bde50c79387e151f8f86079b601", ECKeyProcessor.Keccak256(metadata.toJsonRmSign()));
        System.out.println("v2:" + b);

        System.out.println("from data sign:" + metadata.getSignature());
        System.out.println("dna:" + DTCPProcessor.GeneratorDNA(metadata.getSignature()));
        System.out.println("gen dna from sign_str:" + DTCPProcessor.GeneratorDNA("ffd1515581f7962444291faf67f27f3ef13b9401f52ba076f2cd5b25f88341a923492d08b82cf7b6801c5d8e840bc771d960e74bde50c79387e151f8f86079b600"));
        System.out.println("from data dna:" + metadata.getDna());


//        try {
//            MetadataSaveResp resp = NodeProcessor.SaveMetadata(URL, null, metadata);
//            if (resp == null) {
//                System.out.println("结果体转换异常");
//            }
//            else if (Constants.CODE_ERROR.equalsIgnoreCase(resp.getCode())) {
//                System.out.println("注册异常:"+ resp.getMsg());
//            }
//            else {
//                System.out.println("注册成功。"+ resp.getData().getDna());
//            }
//        } catch (InvalidException e) {
//            e.printStackTrace();
//        }


//        md := &kts.Metadata{
//            Abstract:"原本链是一个分布式的底层数据网络；原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别",
//                    BlockHash:"4D36473D2FF1FE0772A6C0C55D7911295D8E1E27",
//                    Category:"原本,数据,DNA,安全,区块",
//                    ContentHash:"54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526",
//                    Created:"1522067969129",
//                    ID:"4c9b26e165344cf391822cb4c221e8b5",
//                    Language:"zh-cn",
//                    License: struct {
//                Type       string            `json:"type,omitempty" binding:"required"`
//                Parameters map[string]string `json:"parameters,omitempty" binding:"required"`
//            }{Type: "cc", Parameters: map[string]string{
//                "y": "4",
//                        "b": "2",
//            }},
//            PubKey:"03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77",
//                    Title:"原本链java版本sdk测试",
//                    Type:"article",
//                    Signature:"ffd1515581f7962444291faf67f27f3ef13b9401f52ba076f2cd5b25f88341a923492d08b82cf7b6801c5d8e840bc771d960e74bde50c79387e151f8f86079b601",
//                    DNA:"65SO0BNCXRLWIEIKVFSZAAL8WG2964P91N3S29T8HS3YP1RQ67",
//        }
    }

    private static void test4() {
        String str = "9eda9afe54859080783c288fab3bdd3e78dda8878b33359a7e1ef0d4818e1ce0";
        String priKey = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";

        try {
            System.out.println(DTCPProcessor.GeneratorDNA(str));
            System.out.println(ECKeyProcessor.GetPubKeyFromPri(priKey));
            System.out.println(ECKeyProcessor.Sign(priKey, str.getBytes()));
        } catch (InvalidException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ChechBlockHashTest();
//        test4();
//        try {
//            test3();
//        } catch (InvalidException e) {
//            e.printStackTrace();
//        }
//        SaveMetadataTest();
//        String s = "878c162b8bc31d5539c2200955caba4f4e9295041f3bdb51458eb0d56140607f439413e2086d279d732d285b52fa509ee3b85ddbe10eece2a0909b0b22d0d8c800";
//
//        try {
//            System.out.println(DTCPProcessor.GeneratorDNA(s));
//        } catch (InvalidException e) {
//            e.printStackTrace();
//        }
    }
}
