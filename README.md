# services
Yuanben Chain SDK for Java developers

jdk version ：1.7

***
## jar
build JAR : `maven clean package`

[API Documents](http://yuanbenlian.mydoc.io/docs/api.md?t=268053)


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
