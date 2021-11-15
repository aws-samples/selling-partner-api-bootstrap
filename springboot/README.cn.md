## 代码部署

* 代码分成两个部分，一个是CDK，一个是Lambda，分别在各自目录下，分别有README。
* 先搭建CDK的开发环境，这个参考官方文档即可，注意开发机需要配置有足够的AWS权限。
* 接着按CDK目录中README的要求安装CDK的依赖。
* Build Java工程安装了gradlew的话“./gradlew buildZip”，或者用系统的”gradle buildZip” 
* 部署前：请检查CDK中需要的资源，区域（默认us-east-1），然后执行下一步部署。
* 部署：在CDK的目录下执行cdk deploy --all。如果第一次执行可能会报错，错误信息中会说要执行初始化指令，按照提示执行即可。CDK会将数据库，网络，安全，lambda，ElasticCache，权限等配置好。
* 第一次部署需要30分钟以上。结束后直接就可以在CloudWatch中看Lambda输出的日志了。

## 代码配置

* 部署后需要在DynamoDB中的Seller secrets表中加入Seller授权的refreshToken, Region

## 测试

* 在API中调用测试Get方法去订阅ORDER更新事件。

## TODO

* Notification API需要避免重复订阅 - 处理HTTP 409异常，或者提前检查订阅情况。 
* 支持基于Sandbox的UT。

* SellersApi.getMarketplaceParticipations 存储结果