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

import com.yuanbenlian.common.InvalidException;
import com.yuanbenlian.crypto.HexUtil;
import com.yuanbenlian.model.SecretKey;
import com.yuanbenlian.service.ECKeyProcessor;
import org.junit.Test;

/**
 * ECKeyProcessor Test
 */
public class KeyTest {

    public String private_key = "3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c";
    public String public_key = "03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77";
    public String content = "原本链是一个分布式的底层数据网络；" +
            "原本链是一个高效的，安全的，易用的，易扩展的，全球性质的，企业级的可信联盟链；" +
            "原本链通过智能合约系统以及数字加密算法，实现了链上数据可持续性交互以及数据传输的安全；" +
            "原本链通过高度抽象的“DTCP协议”与世界上独一无二的“原本DNA”互锁，确保链上数据100%不可篡改；" +
            "原本链通过优化设计后的共识机制和独创的“闪电DNA”算法，已将区块写入速度提高至毫秒级别";
    public String sign_msg = "b7a59601d0a45ff33c93a61709fbc7586afbb952efb7eed19b348e44caa1fdbd6fbb963d4cb2fd58a128e5831a6f05e05e5064b12cfb3e44842b98a6abb2841c00";

    //examples result:
    //private_key:3c4dbee4485557edce3c8878be34373c1a41d955f38d977cfba373642983ce4c
    //public_key: 03d75b59a801f6db4bbb501ff8b88743902aa83a3e54237edcd532716fd27dea77
    @Test
    public void GeneratorSecp256k1KeyTest() {
        SecretKey secretKey = ECKeyProcessor.GeneratorSecp256k1Key();
        assert secretKey != null;
        System.out.println("private_key:" + secretKey.getPrivateKey() +
                " \npublic_key: " + secretKey.getPublicKey());
    }

    @Test
    public void GetPubKeyFromPriTest() {
        try {
            assert public_key.equals(ECKeyProcessor.GetPubKeyFromPri(private_key)) : "incorrect private key";
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Address() {
        try {
            assert "0x22207149C3B431B2DF3829B49404B1BC31211AAE".equals(ECKeyProcessor.Address(public_key)) : "Incorrect public key";
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    //examples result:
    @Test
    public void SignTest() {
        try {
            assert "89ab153d4f80565abd8f13e702106972fd2ac3a1975c9e7f5963c39ebd94a8eb452e6404e74ad391a74a4c9076f8e5a3f48bf5e16e9f355be835b5b63efa424200"
                    .equals(ECKeyProcessor.Sign(private_key, "hello world".getBytes()));
        } catch (InvalidException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void VerifySignatureTest() {
        try {
            sign_msg = "89ab153d4f80565abd8f13e702106972fd2ac3a1975c9e7f5963c39ebd94a8eb452e6404e74ad391a74a4c9076f8e5a3f48bf5e16e9f355be835b5b63efa424200";
            content = "hello world";
            assert  ECKeyProcessor.VerifySignature(public_key, sign_msg, ECKeyProcessor.Keccak256(content)) : "invalid signature";
        } catch (InvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Keccak256Test() {
        assert "54ce1d0eb4759bae08f31d00095368b239af91d0dbb51f233092b65788f2a526".equals(HexUtil.bytesToHex(ECKeyProcessor.Keccak256(content))) : "Hash error";
    }

}
