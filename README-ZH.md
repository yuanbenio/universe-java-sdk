![原本链](https://github.com/yuanbenio/universe-java-sdk/blob/master/img/yuanbenlian.png)

>这个版本的SDK用来给java语言开发者提供便捷生成metadata的服务。方法的具体使用请查看[examples](https://github.com/yuanbenio/universe-java-sdk/tree/master/src/test/com/yuanben/test)

> 原本链测试地址：https://testnet.yuanbenlian.com

### git路径：
```
https://github.com/yuanbenio/universe-java-sdk
```
**NOTE** 原本链中所有字节数组都以16进制的字符串存储，公钥为压缩格式。

### 服务方法分布
>  Java-SDK提供三个处理器来生成metadata的相关参数：service/KeyProcessor、service/DTCPProcessor、service/NodeProcessor。

```
1. service/KeyProcessor
  这是一个密钥处理器，支持密钥对生成、签名、签名验证以及通过私钥推导公钥。
2. service/DTCPProcessor
  这是一个DTCP协议的处理器，可以用来计算metadata中的各项参数。
3. service/NodeProcessor
  这是一个对接原本链node节点的处理器，可以对node节点发送http请求，主要用于注册metadata以及查询metadata、license和blockHash等数据。 
```
**NOTE** 哈希函数的源码见com.yuanbenlian.crypto，原本链的公私密钥检查工具见:com.yuanben.util.SecretUtil.java
***

### metadata介绍

| name           | type    | comment                                  |source|
| -------------- | ------- | ---------------------------------------- |------|
| type           | string  | 类型, image,article,audio,vedio,custom,private |用户传入|
| language       | string  | 语言 'zh-CN',                              |默认zh-cn,可用户传入|
| title          | string  | 内容标题                                       |用户传入|
| signature      | string  | 内容签名, 算法(secp256k1)                      |系统生成|
| abstract       | string  | 描述,内容摘要                                       |用户传入，为空时，系统自动取内容的前200个字符|
| category       | string  | 分类集, 以逗号分隔 "新闻, 商业"                      |用户传入，如果有传入content，则系统会追加五个|
| dna            | string  | metadata dna                             |系统生成|
| parent_dna            | string  | 该metadata修改前的dna                             |用户传入，如果时修改前一个metadata的数据，则需要传入前一个metadata的dna|
| block_hash            | string  | 区块链上的一个block_hash值                             |用户传入，会到链上做校验|
| created        | integer | 创建的时间,时间戳,10位长度, 1506302092               |系统生成|
| content_hash   | string  | 内容哈希,hash算法(keccak256)                   |可用户传入，如果没有，系统根据content生成|
| block_height            | string  | block_hash对应的区块的height值                             |用户传入，会到链上做校验|
| extra          | TreeMap<String, Object>  | 扩展内容,自定义内容。     |用户传入|
| license        | Metadata.License  | 许可证                                      |用户传入|
| license.type   | string  | 许可证类型                                    |用户传入|
| license.parameters | TreeMap<String, Object>  | 许可证参数对象,自定义内容                            |用户传入|
| source         | string  | 原内容的链接, article,image的官网或者内容的链接                |用户传入|
| data           | TreeMap<String, Object>  | 存放和原数据相关的内容。|用户传入|


## 数据流图
![数据流图](https://github.com/yuanbenio/universe-java-sdk/blob/master/img/数据流图.png)

### API详解

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
> 该方法位于KeyProcessor.java，返回公私密钥，其中私钥为16进制的字符串，长度64，公钥为压缩格式的16进制字符串，长度66。

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
> 该方法位于KeyProcessor.java，需要传入16进制的私钥字符串，返回压缩格式的公钥。

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
> 该方法位于KeyProcessor.java，需要传入16进制的私钥字符串和需要签名的内容，返回16进制的签名字符串。

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
> 该方法位于KeyProcessor.java，需要传入公钥、签名和原数据，返回签名验证结果。

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
> 该方法位于KeyProcessor.java，需要传入需要哈希的数据，返回哈希值。

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
> 该方法位于DTCPProcessor.java，需要传入metadata的Content，返回contentHash。

***
#### GenMetadataSignature
```Java
   /**
     * 对metadata签名 （签名内容为metadata中除去dna\content和signature字段外的所有字段值)
     * @param metadata metadata实例
     * @param privateKey 16进制的私钥
     * @return 16进制的metadata signature
     * @throws InvalidException 入参为空
     */
    public static String GenMetadataSignature(Metadata metadata, String privateKey) throws InvalidException {
        if (metadata == null || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("metadata or privateKey is illegal");
        }
        return ECKeyProcessor.Sign(privateKey, metadata.toJsonRmSign().getBytes());
    }
```
> 该方法位于DTCPProcessor.java，需要传入metadata，返回metadata的签名。

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
> 该方法位于DTCPProcessor.java，需要传入metadata的Signature，返回DNA。

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
> 该方法位于DTCPProcessor.java，需要传入metadata，返回metadata的签名验证结果。

***
#### FullMetadata
```Java
   /**
     * 对metadata进行补全
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata  必须包含license\title\type\block_hash,如果contentHash为空，则必须传入content的值；如果type不是article，则必须传入contentHash
     * @return 信息补全的metadata
     * @throws InvalidException
     */
    public static Metadata FullMetadata(String privateKey, Metadata metadata) throws InvalidException {
         ......
    }
```
> 该方法位于DTCPProcessor.java，需要传入metadata和私钥，返回可被node节点接收的完整metadata。

***
#### FullMetadata
```Java
   /**
     * 对metadata进行补全
     * @param privateKey 16进制的私钥，用于签名
     * @param metadata  必须包含license\title\type\block_hash,如果contentHash为空，则必须传入content的值；如果type不是article，则必须传入contentHash
     * @return 信息补全的metadata
     * @throws InvalidException
     */
    public static Metadata FullMetadata(String privateKey, Metadata metadata) throws InvalidException {
         ......
    }
```
> 该方法位于DTCPProcessor.java，需要传入metadata和私钥，返回可被node节点接收的完整metadata。

***
#### GenRegisterAccountReq
```Java
   /**
     * 生成用于注册公钥的请求体
     *
     * @param privateKey 16进制私钥
     * @param subPubKeys 需要注册的公钥数组
     * @return 请求体封装
     * @throws InvalidException 参数错误
     */
    public static RegisterAccountReq GenRegisterAccountReq(String privateKey, String[] subPubKeys) throws InvalidException {
        if (subPubKeys == null || subPubKeys.length < 1 || !SecretUtil.CheckPrivateKey(privateKey)) {
            throw new InvalidException("subPubKeys or privateKey is illegal");
        }
        RegisterAccountReq req = new RegisterAccountReq();
        req.setSubPubKeys(subPubKeys);
        String pubKey = ECKeyProcessor.GetPubKeyFromPri(privateKey);
        String sign = ECKeyProcessor.Sign(privateKey, JSONArray.toJSONString(subPubKeys).getBytes());
        req.setPubKey(pubKey);
        req.setSignature(sign);
        return req;
    }
```
> 该方法位于NodeProcessor.java，需要传入16进制的私钥和需要注册的公钥，返回用于注册的请求体。

***
#### SaveMetadata
```Java
  /**
     * 向node节点注册metadata
     *
     * @param url     node节点的地址 （http://119.23.22.129:9000)
     * @param version node节点的版本 （默认v1)
     * @param async   是否异步发送 默认async=true为异步发送,如果async=false为同步发送
     * @param md      要注册的metadata，不需要传content
     * @return metadata的注册结果体
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static MetadataSaveResp SaveMetadata(String url, String version, Boolean async, Metadata md) throws InvalidException {
        if (StringUtils.isBlank(url)) {
            throw new InvalidException("url is empty");
        }
        if (StringUtils.isBlank(version)) {
            version = "v1";
        }
        if (async == null ){
            async = true;
        }
        url += "/" + version + "/metadata?async=" + async;
        if (md == null) {
            throw new InvalidException("metadata is null");
        }
        if (StringUtils.isBlank(md.getSignature())) {
            throw new InvalidException("signature is null");
        }
        if (md.getLicense() == null || StringUtils.isBlank(md.getLicense().getType()) || MapUtils.isEmpty(md.getLicense().getParameters())) {
            throw new InvalidException("license is null");
        }
        String s = HttpUtil.sendPost(url, md.toJson());
        return JSONObject.parseObject(s, MetadataSaveResp.class);
    }

```
> 该方法位于NodeProcessor.java，需要传入metadata，注册成功则返回metadata的dna。

***
#### QueryLicense
```Java
  /**
     * 向node节点查询license
     *
     * @param url         node节点的地址 （http://119.23.22.129:9000)
     * @param version     node节点的版本 （默认v1)
     * @param licenseType license's type
     * @return license的查询结果体
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static LicenseQueryResp QueryLicense(String url, String version, String licenseType) throws InvalidException {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(licenseType)) {
            throw new InvalidException("url or licenseType is empty");
        }
        if (StringUtils.isBlank(version)) {
            version = "v1";
        }
        url += "/" + version + "/license/" + licenseType;
        String s = HttpUtil.sendGet(url);
        return JSONObject.parseObject(s, LicenseQueryResp.class);

    }
```
> 该方法位于NodeProcessor.java，需要传入license的type，返回license的详细信息。

***
#### QueryLatestBlockHash
```Java
 /**
     * 向node节点查询最新的blockHash
     *
     * @param url     node节点的地址 （http://119.23.22.129:9000)
     * @param version node节点的版本 （默认v1)
     * @return 最新的blcokHash封装
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static BlockHashQueryResp QueryLatestBlockHash(String url, String version) throws InvalidException {
        if (StringUtils.isBlank(url)) {
            throw new InvalidException("url is empty");
        }
        if (StringUtils.isBlank(version)) {
            version = "v1";
        }
        url += "/" + version + "/block_hash/";
        String s = HttpUtil.sendGet(url);
        return JSONObject.parseObject(s, BlockHashQueryResp.class);
    }
```
> 该方法位于NodeProcessor.java，用于查询原本链最新的区块信息。
**NOTE** 原本链的处理的速度为毫秒级，由于网络延迟，获取到的可能不是最新的区块信息。该接口获取的值主要用于metadata中值的填充，只需要保证hash在链上即可，不需要最新的。

***
#### CheckBlockHash
```Java
   /**
     * 向node节点查询blockHash是否在链上，并处于指定高度
     *
     * @param url     node节点的地址 （http://119.23.22.129:9000)
     * @param version node节点的版本 （默认v1)
     * @param req     请求体 （包括blockHash和blockHeight)
     * @return 查询结果封装
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static BlockHashCheckResp CheckBlockHash(String url, String version, BlockHashCheckReq req) throws InvalidException {
        if (StringUtils.isBlank(url) || req == null || StringUtils.isBlank(req.getHash())) {
            throw new InvalidException("url or request body is empty");
        }
        if (StringUtils.isBlank(version)) {
            version = "v1";
        }
        url += "/" + version + "/check_block_hash/";
        String s = HttpUtil.sendPost(url, req.toJson());
        return JSONObject.parseObject(s, BlockHashCheckResp.class);
    }
```
> 该方法位于NodeProcessor.java，用于检查blockHash是否在链上，并处于指定高度。

***
#### RegisterAccount
```Java
   /**
     * 注册公钥
     *
     * @param url     node节点的地址 （http://localhost:9000)
     * @param version node节点的版本 （默认v1)
     * @param req     请求体
     * @return 返回结果封装
     * @throws InvalidException 参数有误或网络请求错误
     */
    public static RegisterAccountResp RegisterAccount(String url, String version, RegisterAccountReq req) throws InvalidException {
        if (StringUtils.isBlank(url) || req == null) {
            throw new InvalidException("url or request body is empty");
        }
        if (!SecretUtil.CheckPublicKey(req.getPubKey(), true) ||
                StringUtils.isEmpty(req.getSignature()) ||
                req.getSubPubKeys() == null || req.getSubPubKeys().length < 1) {
            throw new InvalidException("request parameters error");
        }
        if (StringUtils.isBlank(version)) {
            version = "v1";
        }
        url += "/" + version + "/accounts/";
        String s = HttpUtil.sendPost(url, req.toJson());
        return JSONObject.parseObject(s, RegisterAccountResp.class);
    }
```
> 该方法位于NodeProcessor.java，用于注册公钥，返回体的code为ok，则表示注册成功。

