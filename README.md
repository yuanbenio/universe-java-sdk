# services
SDK服务
***
## jar
sdk为maven项目，请使用idea中的maven打包或者使用命令：mvn clean package打包即可，会在target下生成jar包

## API文档说明
这个版本的SDK用来给java语言开发者提供便捷生成metadata的服务。

git路径：https://git.dev.yuanben.org/scm/unv/universe-java-sdk.git

**NOTE** 原本链中所有字节数组都以16进制的字符串存储，公钥为压缩格式。

#### 服务方法分布
  Java-SDK提供两个处理器来生成metadata的相关参数：service/KeyProcessor、service/DTCPProcessor。
1. service/KeyProcessor
  这是一个密钥处理器，支持密钥对生成、签名、签名验证以及通过私钥推导公钥。
2. service/DTCPProcessor
  这是一个DTCP协议的处理器，可以用来计算metadata中的各项参数。 

**NOTE** 哈希函数的源码见com.yuanben.crypto，原本链的公私密钥检查工具见:com.yuanben.util.SecretUtil.java
***
#### GeneratorSecp256k1Key
```Java
/**
     * 生成密钥对
     *
     * @return secp256k1密钥
     */
public static SecretKey GeneratorSecp256k1Key() {
        SecretKey secretKey = new SecretKey();
        ECKey ecKey = new ECKey();
        secretKey.setPrivateKey(Hex.toHexString(ecKey.getPrivKeyBytes()));
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();
        byte[] encoded = pubKeyPoint.getEncoded(true);
        secretKey.setPublicKey(Hex.toHexString(encoded));
        return secretKey;
}
```
该方法位于KeyProcessor.java，返回公私密钥，其中私钥为16进制的字符串，长度64，公钥为压缩格式的16进制字符串，长度66。
***
#### GetPubKeyFromPri
```Java
/**
     * 根据私钥生成16进制对压缩公钥
     *
     * @param privateKey 16进制私钥
     * @return 16进制的公钥
     */
public static String GetPubKeyFromPri(String privateKey) throws InvalidException {
        if (!SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("private key`s format is error");
        }
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        ECPoint pubKeyPoint = ecKey.getPubKeyPoint();
        byte[] encoded = pubKeyPoint.getEncoded(true);
        return Hex.toHexString(encoded);
}
```
该方法位于KeyProcessor.java，需要传入16进制的私钥字符串，返回压缩格式的公钥。
***
#### Sign
```Java
/**
     * 签名
     *
     * @param privateKey 私钥
     * @param content    签名内容 字节数组
     * @return 16进制签名串
     */
    public static String Sign(String privateKey, byte[]... content) throws InvalidException {
        if (!SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("private key`s format is error");
        }
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        Keccak256 keccak256 = new Keccak256();
        for (byte[] b : content) {
            keccak256.update(b);
        }
        ECKey.ECDSASignature ecdsaSignature = ecKey.doSign(keccak256.digest());
        return ecdsaSignature.toHex();
    }
```
该方法位于KeyProcessor.java，需要传入16进制的私钥字符串和需要签名的内容，返回16进制的签名字符串。
***
#### VerifySignature
```Java
    /**
     * 签名验证
     *
     * @param publicKey 16进制的压缩公钥
     * @param signMsg   16进制的签名串
     * @param data      被签名的原数据字节数组 keccak256哈希值
     * @return 验证结果
     */
    public static boolean VerifySignature(String publicKey, String signMsg, byte[] data) throws InvalidException {
        if (!SecretUtil.CheckPublicKey(publicKey, true)) {
            throw new InvalidException("public key`s format is error");
        }

        byte[] decode = Hex.decode(signMsg);
        byte[] rBs = new byte[decode.length / 2];
        byte[] sBs = new byte[decode.length / 2];

        System.arraycopy(decode, 0, rBs, 0, decode.length / 2);
        System.arraycopy(decode, decode.length / 2, sBs, 0, decode.length / 2);

        BigInteger r = new BigInteger(Hex.toHexString(rBs), 16);
        BigInteger s = new BigInteger(Hex.toHexString(sBs), 16);

        ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(r.toByteArray(), s.toByteArray(), (byte) 0x1b);
        return ECKey.verify(data, sig, Hex.decode(publicKey));
    }
```
该方法位于KeyProcessor.java，需要传入公钥、签名和原数据，返回签名验证结果。
***
#### Keccak256
```Java
   /**
     * keccak256哈希运算
     *
     * @param content 内容
     * @return 哈希值 length=32
     */
    public static byte[] Keccak256(byte[]... content) {
        if (content == null) return null;
        Keccak256 keccak256 = new Keccak256();
        for (byte[] s : content) {
            keccak256.update(s);
        }
        return keccak256.digest();
    }

    /**
     * keccak256哈希运算
     *
     * @param content 内容
     * @return 哈希值 length=32
     */
    public static byte[] Keccak256(String... content) {
        if (content == null) return null;
        Keccak256 keccak256 = new Keccak256();
        for (String s : content) {
            keccak256.update(s.getBytes());
        }
        return keccak256.digest();
    }
```
该方法位于KeyProcessor.java，需要传入需要哈希的数据，返回哈希值。
***
#### GenContentHash
```Java
   /**
     * 生成contentHash
     * contentHash = Keccak256(content)
     * @return 16进制的contentHash
     */
    public static String GenContentHash(String... content) {
        if (content == null) return Constants.STRING_EMPTY;
        Keccak256 keccak256 = new Keccak256();
        for (String s : content) {
            keccak256.update(s.getBytes());
        }
        return Hex.toHexString(keccak256.digest());
    }
```
该方法位于DTCPProcessor.java，需要传入metadata的Content，返回contentHash。
***
#### GeneratorDNA
```Java
   /**
     * 生成闪电dna
     * @param signature 16进制的metadata签名串
     * @return metadata的闪电dna
     * @throws InvalidException 入参为空
     */
    public static String GeneratorDNA(String signature) throws InvalidException {
        if (StringUtils.isBlank(signature)) {
            throw new InvalidException("signature is empty");
        }
        Keccak256 keccak256 = new Keccak256();
        keccak256.update(signature.getBytes());
        return Hex.toHexString(keccak256.digest());
    }
```
该方法位于DTCPProcessor.java，需要传入metadata的Signature，返回DNA。
***
#### VerifyMetadataSignature
```Java
   /**
     * 对metadata进行签名验证 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * @param metadata metadata
     * @return 验证结果
     * @throws InvalidException metadata为空
     */
    public static boolean VerifyMetadataSignature(Metadata metadata) throws InvalidException {
            if (metadata == null ) {
                throw new InvalidException("metadata is null");
            }
            return ECKeyProcessor.VerifySignature(metadata.getPubKey(),metadata.getSignature(), ECKeyProcessor.Keccak256(metadata.toJsonRmSign()));
        }
```
该方法位于DTCPProcessor.java，需要传入metadata，返回metadata的签名验证结果。
***
#### GenMetadataFromContent
```Java
   /**
     * 对metadata进行补全
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata  必须包含content\title\type
     * @return 信息补全的metadata
     * @throws InvalidException
     */
    public static Metadata GenMetadataFromContent(String privateKey, Metadata metadata) throws InvalidException {
         ......
    }
```
该方法位于DTCPProcessor.java，需要传入metadata和私钥，返回可被node节点接收的完整metadata。
***










