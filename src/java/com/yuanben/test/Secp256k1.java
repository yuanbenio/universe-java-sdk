package com.yuanben.test;


import com.hankcs.hanlp.HanLP;
import com.yuanben.common.InvalidException;
import com.yuanben.crypto.ECKey;
import com.yuanben.crypto.ECKey.ECDSASignature;
import com.yuanben.crypto.cryptohash.Keccak256;
import com.yuanben.model.Metadata;
import com.yuanben.service.ECKeyProcessor;
import org.spongycastle.asn1.*;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Secp256k1 {
    private static String priKey = "af7a3e2004b35f73e9bada891563d6522847b98e0edf7c12af6f40bf859da24c";
    private static String pubKey = "0264d6d0b926a6382eb227f7b233bdaa98c4b51998322831cf197f3ebe760e096b";
    private static String content = "有关所有现有选项的参考，其描述和默认值，可以参考默认配置ethereumj.conf（可以在库jar或源树中找到它ethereum-core/src/main/resources）。要覆盖需要的选项，可以使用以下方法之一：";
    private static String signatureMsg = "010147bc3a3b896aeb9be06456e1a1f6dac582f242b97318ff12df607d8b2ce97f60728a18a6be7e3bd6f5faf53724aa9511fa1f7bfa6b6544dd489f2c7b764700";
    private static String signBase64 = "AAEBR7w6O4lq65vgZFbhofbaxYLyQrlzGP8S32B9iyzpf2Byihimvn471vX69TckqpUR+h97+mtlRN1Inyx7dkc=";
    private static String contentHash = "58449825e30c3c9865321d21912818ea25e879dfedefa0461518eb0c82bb6059";

    private static void test1() {

        ECKey ecKey = new ECKey();
        System.out.println(Hex.toHexString(ecKey.getPrivKeyBytes()));
        System.out.println(ecKey.getPrivKeyBytes().length);

        System.out.println(ecKey.isCompressed());
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();


        System.out.println("publicLen:" + Hex.toHexString(ecKey.getPubKey()).length());
        System.out.println("publicLen:" + ecKey.getPubKey().length);

        byte[] encoded = pubKeyPoint.getEncoded(true);

        System.out.println("com publicLen:" + Hex.toHexString(encoded).length());
        System.out.println("com publicLen:" + encoded.length);
        System.out.println(Hex.toHexString(encoded));
        System.out.println(Hex.toHexString(ecKey.getPubKey()));
    }

    private static void test2() {
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(priKey));

        Keccak256 keccak256 = new Keccak256();
        keccak256.update(content.getBytes());
        byte[] h = keccak256.digest();
        System.out.println(Hex.toHexString(h));
        ECDSASignature ecdsaSignature = ecKey.doSign(h);
        String s = ecdsaSignature.toHex();

        System.out.println(ecdsaSignature.v);
        System.out.println(ecdsaSignature.r);
        System.out.println(ecdsaSignature.s);

        System.out.println(ecdsaSignature.toBase64());

    }

    private static void test3() {
        BigInteger r = new BigInteger("454574794241245257710862012216869928730068335843842906507617311289361771753", 16);
        BigInteger s = new BigInteger("57614139610445819959896919479409577201958225736645311280222886092724565145159", 16);
        ECDSASignature sig = ECDSASignature.fromComponents(r.toByteArray(), s.toByteArray(), (byte) 0x1b);

        ECKey ecKey = ECKey.fromPublicOnly(Hex.decode(pubKey));
        byte[] decode = Hex.decode(signatureMsg);
        byte[] buf = new byte[decode.length - 1];
        System.arraycopy(decode, 0, buf, 0, decode.length - 1);
        System.out.println("decode:" + Hex.toHexString(decode) + " \nbuf:   " + Hex.toHexString(buf));
//        System.out.println(ecKey.verify(Hex.decode(contentHash),buf));


        try {
            System.out.println(Hex.toHexString(ECKey.signatureToKey(Hex.decode(contentHash), signBase64).getPubKey()));
        } catch (SignatureException e) {
            e.printStackTrace();
        }
      /*  try {
            ECKey ecKey1 = ECKey.signatureToKey(Hex.decode(contentHash), sig);
            boolean verify = ecKey1.verify(Hex.decode(contentHash), sig);
            System.out.println(verify);
        } catch (SignatureException e) {
            e.printStackTrace();
        }*/


    }

    private static void test4() {
        try {
            ASN1InputStream asn1InputStream = new ASN1InputStream(Hex.decode("010147bc3a3b896aeb9be06456e1a1f6dac582f242b97318ff12df607d8b2ce97f60728a18a6be7e3bd6f5faf53724aa9511fa1f7bfa6b6544dd489f2c7b764700"));
            //将hex转换为byte输出
            ASN1Primitive asn1Primitive = null;
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                //循环读取，分类解析。这样的解析方式可能不适合有两个同类的ASN1对象解析，如果遇到同类，那就需要按照顺序来调用readObject，就可以实现解析了。
                if (asn1Primitive instanceof ASN1Integer) {
                    ASN1Integer asn1Integer = (ASN1Integer) asn1Primitive;
                    System.out.println("Integer:" + asn1Integer.getValue());
                } else if (asn1Primitive instanceof ASN1Boolean) {
                    ASN1Boolean asn1Boolean = (ASN1Boolean) asn1Primitive;
                    System.out.println("Boolean:" + asn1Boolean.isTrue());
                } else if (asn1Primitive instanceof ASN1Sequence) {
                    ASN1Sequence asn1Sequence = (ASN1Sequence) asn1Primitive;
                    ASN1SequenceParser asn1SequenceParser = asn1Sequence.parser();
                    ASN1Encodable asn1Encodable = null;
                    while ((asn1Encodable = asn1SequenceParser.readObject()) != null) {
                        asn1Primitive = asn1Encodable.toASN1Primitive();
                        if (asn1Primitive instanceof ASN1String) {
                            ASN1String string = (ASN1String) asn1Primitive;
                            System.out.println("PrintableString:" + string.getString());
                        } else if (asn1Primitive instanceof ASN1UTCTime) {
                            ASN1UTCTime asn1utcTime = (ASN1UTCTime) asn1Primitive;
                            System.out.println("UTCTime:" + asn1utcTime.getTime());
                        } else if (asn1Primitive instanceof ASN1Null) {
                            System.out.println("NULL");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void test5() {
        BigInteger r = new BigInteger("c52c114d4f5a3ba904a9b3036e5e118fe0dbb987fe3955da20f2cd8f6c21ab9c", 16);
        BigInteger s = new BigInteger("6ba4c2874299a55ad947dbc98a25ee895aabf6b625c26c435e84bfd70edf2f69", 16);

        byte[] decode = Hex.decode(signatureMsg);
        byte[] rBs = new byte[decode.length / 2];
        byte[] sBs = new byte[decode.length / 2];

        System.arraycopy(decode, 0, rBs, 0, decode.length / 2);
        System.arraycopy(decode, decode.length / 2, sBs, 0, decode.length / 2);

        r = new BigInteger(Hex.toHexString(rBs), 16);
        s = new BigInteger(Hex.toHexString(sBs), 16);


        ECDSASignature sig = ECDSASignature.fromComponents(r.toByteArray(), s.toByteArray(), (byte) 0x1b);
        byte[] rawtx = Hex.decode("f82804881bc16d674ec8000094cd2a3d9f938e13cd947ec05abc7fe734df8dd8268609184e72a0006480");
        Keccak256 keccak256 = new Keccak256();
        keccak256.update(rawtx);
        byte[] rawHash = keccak256.digest();
        byte[] address = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
        try {
            ECKey key = ECKey.signatureToKey(rawHash, sig);

            System.out.println("Signature public key\t: " + Hex.toHexString(key.getPubKey()));
            System.out.println("Sender is\t\t: " + Hex.toHexString(key.getAddress()));


            System.out.println(key.verify(rawHash, sig));
        } catch (SignatureException e) {
        }
    }

    private static void test6() {

        Keccak256 keccak256 = new Keccak256();
        keccak256.update(content.getBytes());
        byte[] s = keccak256.digest();
        System.out.println(s.length);

    }

    private static void test7() {
        Metadata metadata = new Metadata();
        metadata.setAbstractContent("sagdsfg");
        metadata.setDna("afsdfdfa");
        Metadata.License license = new Metadata.License();
        license.setType("cc");
        Map<String, String> params = new HashMap<>();
        params.put("data", "afgad");
        params.put("cata", "afgad");
        params.put("aata", "afgad");
        license.setParameters(params);
        metadata.setLicense(license);
        System.out.println(metadata.toJson());


    }

    private static void test8() {
        try {
            ECKey ecKey = ECKey.fromPrivate(Hex.decode(priKey));

            System.out.println(ECKeyProcessor.VerifySignature(Hex.toHexString(ecKey.getPubKey()), signatureMsg, Hex.decode(contentHash)));
        } catch (InvalidException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        String document = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
                "算法可以宽泛的分为三类，\n" +
                "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
                "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
                "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
        List<String> sentenceList = HanLP.extractSummary(document, 3);
        System.out.println(sentenceList);


    }

}
