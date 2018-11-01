# services
SDK服务 

jdk版本：1.7

***
## jar
sdk为maven项目，请使用idea中的maven打包或者使用命令：mvn clean package打包即可，会在target下生成jar包

详情请阅读[API说明文档](http://yuanbenlian.mydoc.io/docs/api.md?t=268053)


## 节点返回的状态码说明

|状态码|状态信息|说明|
|---|---|---|
|ok|success|成功|
|3001|Invalid parameters|有参数没有传，或参数类型错误|
|3002|parameters is empty|必须要传递的参数有为空的|
|3003|record do not exist|记录不存在|
|3004|record already exists|记录已存在|
|3005|Permission denied: please register first|开启了公钥过滤，需要先注册metadata里的公钥|
|3006|Permission denied: please contract yuanbenlian by emial:kooksee@163.com|节点没有开通权限，请联系原本链|
|3007|unmarshal fail|数据格式错误|
|3009|store data fail|数据库存在异常，数据存储失败|
|3010|data not on chain|数据未上链|
|3011|block information is empty|区块不存在|
|3012|query fail:|查询失败|
|3020|signature verify fail|签名验证失败|
|3023|parameters verify fail|签名或DNA验证失败|
|3021|invalid public key|非法的公钥|
|3022|license's parameters is empty|非none协议，license参数不能为空|
|4001|error to connect redis|redis失联|
|4002|error to connect chain node|节点失联|
|4003|broadcast transaction fail|广播交易失败|
|4004|abci query fail|abci查询异常|
|4005|redis handle error|redis操作异常|
|5000|server error|系统未知异常|