# services
Yuanben Chain SDK for Java developers

![Yuanben chain](https://github.com/yuanbenio/universe-java-sdk/blob/master/img/yuanbenlian.png)

jdk version ：1.7

[中文文档](https://github.com/yuanbenio/universe-java-sdk/blob/master/README-ZH.md)


## Download

- [maven][1]
- [the latest JAR][2]  

[1]: https://oss.sonatype.org/content/groups/public/com/yuanbenlian/universe-java-sdk
[2]: https://github.com/yuanbenio/universe-java-sdk/blob/master/jar

## Maven

```xml
<dependency>
  <groupId>com.yuanbenlian</groupId>
  <artifactId>universe-java-sdk</artifactId>
  <version>1.4.1-SNAPSHOT</version>
</dependency>
```

## return code

|code|describe|
|---|---|
|ok|Success|
|3001|Invalid parameter|
|3002|Empty parameter|
|3003|Record does not exist|
|3004|Record already exists|
|3005|Permission denied: please register the public key first|
|3006|Permission denied: please contract Yuanben chain support|
|3007|Incorrect data|
|3009|Data storage fail|
|3010|Data not on YuanBen chain |
|3011|block information is empty|
|3012|Query fail|
|3020|Signature verification fail|
|3023|Parameter verification fail|
|3021|Invalid public key|
|3022|License's parameters are empty|
|4001|Error connecting to redis server|
|4002|Error connecting to the first-level node|
|4003|Broadcast transaction fail|
|4004|ABCI query fail|
|4005|Redis handling error|
|5000|Unknown error|


## Data flow diagram
![Data-flow](https://github.com/yuanbenio/universe-java-sdk/blob/master/img/data-flow.png)


## API Document

> TestNet address: https://testnet.yuanbenlian.com

### Service Introduction

The Java-SDK provides three processors: Service/KeyProcessor, Service/DTCP Processor and Service/NodeProcessor.

```text
1. Service/KeyProcessor
    This is a base service, it supports: generating key pair, calculating signatures, verifying signatures, and more
2. Service/DTCP Processor
    Processing metadata
3. Service/NodeProcessor
    To access the YuanBen Chain Node, supports: query and saving metadata, query licenses, query latest BlockHash、registering public key
```

### Metadata Introduction


| name           | type    | comment              |source|
| -------------- | ------- | ---------------------|------|
| type           | string  | eg:image,article,audio,video,custom,private |user-defined|
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

### API Interface

#### GeneratorSecp256k1Key
```Java
/**
     * Generate a key pair
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
        * completing metadata
        *
        * @param privateKey 
        * @param metadata   include(license\title\type\block_hash|block_height|category.
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
         * query metadata from YuanBen chain
         *
         * @param url node address （http://localhost:9000/v1)
         * @param dna DNA
         * @return result include metadata and transaction information
         * @throws InvalidException 
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
        * register public key
        *
        * @param privateKey 
        * @param subPubKeys 
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

[more examples](https://github.com/yuanbenio/universe-java-sdk/tree/master/src/test/com/yuanbenlian/test) 