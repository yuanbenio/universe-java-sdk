package com.yuanben.test;

import com.yuanben.common.Constants;
import com.yuanben.common.InvalidException;
import com.yuanben.model.Metadata;
import com.yuanben.service.DTCPProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTCPProcessor Test
 */
public class DTCPTest {
    private static String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    private static String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    private static String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    private static String block_hash = "4D36473D2FF1FE0772A6C0C55D7911295D8E1E27";
    private static String sign_msg = "78c9082f22451fb9befd9adc8ebffa6e06599b94d4846cf7e52f45e92e42f16f2e5a8e411b560659735d13f4e2fa79031cf2a8dc752fe21f579c9ab5409e240e00";

    //test result:
    //54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526
    private static void GenContentHashTest () {
        String contentHash = DTCPProcessor.GenContentHash(content);
        System.out.println(contentHash);
    }


    //test result :
    //{"abstract":"原本链是一个分布式的底层数据网络；原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别","block_hash":"4D36473D2FF1FE0772A6C0C55D7911295D8E1E27","category":"原本,数据,DNA,安全,区块","content_hash":"54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526","created":"1521783342747","dna":"b6ba8776813b7cfdc8b47e9c76c7bd6f9edb732257e432e1c8c87514a532e889","id":"6ed8953efca8465e946aa2157c4d5fa3","language":"zh-cn","license":{"parameters":{"b":"2","y":"4"},"type":"cc"},"pubkey":"03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77","signature":"78c9082f22451fb9befd9adc8ebffa6e06599b94d4846cf7e52f45e92e42f16f2e5a8e411b560659735d13f4e2fa79031cf2a8dc752fe21f579c9ab5409e240e00","title":"原本链java版本sdk测试","type":"article"}
    private static Metadata GenMetadataFromContentTest () {
        Metadata metadata = new Metadata();
        metadata.setContent(content);
        metadata.setBlockHash(block_hash);
        metadata.setType(Constants.TYPE_ARTICLE);
        metadata.setTitle("原本链java版本sdk测试");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        Map<String, String> params = new HashMap<>();
        params.put("y", "4");
        params.put("b", "2");
        license.setParameters(params);
        metadata.setLicense(license);

        try {
            metadata = DTCPProcessor.GenMetadataFromContent(private_key, metadata);
            System.out.println(metadata.toJson());
        } catch (InvalidException e) {
            e.printStackTrace();
        }
        return metadata;
    }

    //test result :
    // signature : d6014583bec8e57f3e49e1767d9b7b082b72aba22e7f87c4ab3bd492fd0b53eb1f6c5fe29abc80d28f8ab02094832d0113e4028e214c23317cda60c67eb3020400
    private static void GenMetadataSignatureTest () {
        Metadata metadata = GenMetadataFromContentTest();
        try {
            String signature = DTCPProcessor.GenMetadataSignature(metadata, private_key);
            System.out.println(signature);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }


    //test result :
    //b6ba8776813b7cfdc8b47e9c76c7bd6f9edb732257e432e1c8c87514a532e889
    private static void GeneratorDNATest () {
        try {
            String dna = DTCPProcessor.GeneratorDNA(sign_msg);
            System.out.println(dna);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    //test result :
    //true
    private static void VerifyMetadataSignatureTest () {

        try {
            Metadata metadata = GenMetadataFromContentTest();
            boolean b = DTCPProcessor.VerifyMetadataSignature(metadata);
            System.out.println(b);
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        VerifyMetadataSignatureTest();
    }
}
