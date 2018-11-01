# services
Yuanben Chain SDK for Java developers

![Yuanben chain](https://github.com/yuanbenio/universe-java-sdk/blob/master/yuanbenlian.png)

jdk version ：1.7

***
## jar
build JAR : `maven clean package`

## return code 

|code|describe|
|---|---|
|ok|success|
|3001|invalid parameters|
|3002|The parameter are empty|
|3003|record does not exist|
|3004|record already exists|记录已存在|
|3005|Permission denied: please register the public key first|
|3006|Permission denied: please contract yuanbenlian support|
|3007|incorrect data|
|3009|store data fail|
|3010|data not on Yuanben chain |
|3011|block information is empty|
|3012|query fail:|
|3020|signature verify fail|
|3023|parameters verify fail|
|3021|invalid public key|
|3022|license's parameters is empty|
|4001|error to connect redis server|
|4002|error to connect the first-level node|
|4003|broadcast transaction fail|
|4004|ABCI query fail|
|4005|redis handle error|
|5000|unknown error|


## API Document

> testNet address: https://testnet.yuanbenlian.com

### service introduce

Java-SDK provide three processors:service/KeyProcessor、service/DTCPProcessor、service/NodeProcessor。

```text
1. service/KeyProcessor
    this is a base service, it support: generate key pair、calculating signature、verify signature、etc
2. service/DTCPProcessor
    calculating metadata
3. service/NodeProcessor
    To Access YuanBen Chain Node,support:query and save metadata、query license、query latest blockHash、register public key
```
 
### metadata introduce
 
 | name           | type    | comment                                     |source|
 | -------------- | ------- | ----------------------------------------    |------|
 | type           | string  | eg:image,article,audio,vedio,custom,private |user-defined|
 | language       | string  | 'zh-CN',                                    |default:zh-CN,user-defined|
 | title          | string  | title                                       |user-defined|
 | signature      | string  | sign by secp256k1                           |generate by system|
 | abstract       | string  | Content summary                             |default:content[:200],user-defined|
 | category       | string  | eg:"news"                                   |user-defined，if there is content, the system will add five more|
 | dna            | string  | metadata dna                                |generate by system|
 | parent_dna     | string  | -                                           |user-defined,link an other metadata|
 | block_hash     | string  | block_hash on YuanBen chain                 |user-defined|
 | block_height   | string  | block_hash corresponding block_height       |user-defined|
 | created        | integer | timestamp, eg:1506302092                    |generate by system|
 | content_hash   | string  | Keccak256(content)                          |default:Keccak256(content),user-defined|
 | extra          | TreeMap<String, Object>  | user-defined content       |user-defined|
 | license        | Metadata.License  |                                   |user-defined|
 | license.type   | string  | the type of license                         |user-defined|
 | license.parameters | TreeMap<String, Object>  | the parameters of license   |user-defined|
 | source         | string  | source link.                                |user-defined|
 | data           | TreeMap<String, Object>  | extension data of the type |user-defined|
 
### API详解

#### GeneratorSecp256k1Key
```Java
/**
     * 生成密钥对
     * generate a key pair
     *
     * @return secp256k1 key pair
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
> location:KeyProcessor.java，return a key pair.

***
#### FullMetadata
```Java
   /**
        * 对metadata进行补全
        * completing metadata
        *
        * @param privateKey 16进制的私钥，用于签名
        * @param metadata   必须包含license\title\type\block_hash|block_height\category,如果contentHash为空，则必须传入content的值；如果type不是article，则必须传入contentHash;如果category为空，则必须传入content
        *                   include(license\title\type\block_hash|block_height|category.
        *                   if content is empty,you must pass content;
        *                   if type isn't article,you must pass contentHash;
        *                   if category is empty,you must pass content)
        * @return full metadata
        * @throws InvalidException invalid parameters
        */
       public static Metadata FullMetadata(String privateKey, Metadata metadata) throws InvalidException, UnsupportedEncodingException {
         //......
    }
   
```
> location:DTCPProcessor.java，return a full metadata.

***
#### SaveMetadata
```Java
  /**
       * 向node节点注册metadata
       * submit metadata to YuanBen chain
       *
       * @param url node address（http://localhost:9000/v1)
       * @param md  metadata
       * @return result
       * @throws InvalidException
       */
      public static MetadataSaveResp SaveMetadata(String url, Metadata md) throws InvalidException {
        //......
    }

```
> location:NodeProcessor.java.

***
#### QueryLatestBlockHash
```Java
    /**
        * 向node节点查询license
        * query license to YuanBen chain
        *
        * @param url         node address （http://localhost:9000/v1)
        * @param licenseType license's type
        * @return result
        * @throws InvalidException
        */
       public static LicenseQueryResp QueryLicense(String url, String licenseType, String licenseVersion) throws InvalidException {
           //.....
       }
```
> location:NodeProcessor.java。Because of network latency, it may not be the latest block information. If you can't get it, please fill it with a fixed value. The value of the last request is a good choice.

***
#### QueryMetadata
```Java
    /**
         * 向node节点查询metadata
         * query metadata from YuanBen chain
         *
         * @param url node address （http://localhost:9000/v1)
         * @param dna DNA
         * @return result include metadata and transaction information
         * @throws InvalidException 参数有误或网络请求错误
         */
        public static MetadataQueryResp QueryMetadata(String url, String dna) throws InvalidException {
            if (StringUtils.isBlank(url) || StringUtils.isBlank(dna)) {
                throw new InvalidException("url or DNA is empty");
            }
            url += "/metadata/" + dna;
            String s = HttpUtil.sendGet(url);
            return GsonUtil.getInstance().fromJson(s, MetadataQueryResp.class);
        }
```
> location:NodeProcessor.java。

***
#### GenRegisterAccountReq
```Java
   /**
        * 生成用于注册公钥的请求体
        * register public key
        *
        * @param privateKey 16进制私钥
        * @param subPubKeys 需要注册的公钥数组
        * @return request
        * @throws InvalidException invalid parameters
        */
       public static RegisterAccountReq GenRegisterAccountReq(String privateKey, String[] subPubKeys) throws InvalidException {
           //......
       }
```
> location:DTCPProcessor.java. If the node does not open authentication mode, there is no need to use this method.

***
#### RegisterAccount
```Java
   /**
        * 注册公钥
        * register public key to YuanBen chain
        *
        * @param url node address （http://localhost:9000/v1)
        * @param req request
        * @return result
        * @throws InvalidException
        */
       public static RegisterAccountResp RegisterAccount(String url, RegisterAccountReq req) throws InvalidException {
           //....
       }
```
> location:NodeProcessor.java.


